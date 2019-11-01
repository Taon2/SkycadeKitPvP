package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class KitHulk extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int smashCooldown = 20;

    private Map<UUID, List<FallingBlock>> blockList = new HashMap<>();
    private Map<UUID, Boolean> isSmashing = new HashMap<>();

    public KitHulk(KitManager kitManager) {
        super(kitManager, "Hulk", KitType.HULK, 50000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.BLACK).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.GREEN).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.PURPLE).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .setColour(Color.GREEN).build();

        constantEffects.put(PotionEffectType.SLOW, 1);
        constantEffects.put(PotionEffectType.INCREASE_DAMAGE, 4);

        ItemStack icon = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .setColour(Color.PURPLE).build();
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.AIR)
            return;
        if (!addCooldown(p, "Hulk Smash", smashCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2.0F, 1.0F);
        p.setVelocity(new org.bukkit.util.Vector(0, 3, 0));
        int taskId = Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), () -> {
            ParticleEffect.CLOUD.display(0, 0, 0, 0, 1, p.getLocation(), 128);
        }, 0L, 1L).getTaskId();
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                Bukkit.getScheduler().cancelTask(taskId);
                p.setVelocity(new org.bukkit.util.Vector(0, -3, 0));
                isSmashing.put(p.getUniqueId(), true);
        }, 15L);

        new BukkitRunnable() {
            public void run() {
                if (p.isOnGround()) {
                    onLand(p);
                    this.cancel();
                }
            }
        }.runTaskTimer(getKitManager().getKitPvP(), 15L, 3L);
    }

    private void onLand(Player p) {
        if (isSmashing.containsKey(p.getUniqueId()) && isSmashing.get(p.getUniqueId()) && p.isOnGround()) {
            Location loc = p.getLocation();
            p.getLocation().getWorld().playSound(p.getLocation(), Sound.EXPLODE, 2.0F, 1.0F);

            for (int i = 1; i < 5; i++) {

                for (Block b : getBlocksInRadius(loc.clone().add(0.0D, -1.0D, 0.0D), i, true)) {
                    if (b.getLocation().getBlockY() == loc.getBlockY() - 1 && b.getType() != Material.AIR && b.getType() != Material.SIGN_POST && b.getType() != Material.CHEST && b.getType() != Material.STONE_PLATE && b.getType() != Material.WOOD_PLATE && b.getType() != Material.WALL_SIGN && b.getType() != Material.WALL_BANNER && b.getType() != Material.STANDING_BANNER && b.getType() != Material.CROPS && b.getType() != Material.LONG_GRASS && b.getType() != Material.SAPLING && b.getType() != Material.DEAD_BUSH && b.getType() != Material.RED_ROSE && b.getType() != Material.RED_MUSHROOM && b.getType() != Material.BROWN_MUSHROOM && b.getType() != Material.TORCH && b.getType() != Material.LADDER && b.getType() != Material.VINE && b.getType() != Material.DOUBLE_PLANT && b.getType() != Material.PORTAL && b.getType() != Material.CACTUS && b.getType() != Material.WATER && b.getType() != Material.STATIONARY_WATER && b.getType() != Material.LAVA && b.getType() != Material.STATIONARY_LAVA && b.getType().isSolid() && b.getType().getId() != 43 && b.getType().getId() != 44 && b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                        Bukkit.getScheduler().runTask(KitPvP.getInstance(), () -> {
                            FallingBlock fb = loc.getWorld().spawnFallingBlock(b.getLocation().clone().add(0.0D, 1.100000023841858D, 0.0D), b.getType(), b.getData());
                            fb.setCustomName(p.getName());
                            fb.setCustomNameVisible(false);
                            fb.setVelocity(new Vector(0.0F, 0.3F, 0.0F));
                            fb.setDropItem(false);

                            List<FallingBlock> fallingBlocks = new ArrayList<>();
                            if (blockList.containsKey(p.getUniqueId()))
                                fallingBlocks = blockList.get(p.getUniqueId());
                            fallingBlocks.add(fb);
                            blockList.put(p.getUniqueId(), fallingBlocks);

                            fb.getNearbyEntities(1.0D, 1.0D, 1.0D).stream()
                                    .filter((ent) -> ent != p && ent.getType() != EntityType.FALLING_BLOCK)
                                    .forEach((ent) -> {
                                if (ent.hasMetadata("NPC"))
                                    return;
                                ent.setVelocity(ent.getLocation().getDirection().multiply(-1.35F).setY(0.8F));
                            });
                        });
                    }
                }
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(KitPvP.getInstance(), () -> {
                isSmashing.remove(p.getUniqueId());
            }, 5L);
        }
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (Map.Entry<UUID, List<FallingBlock>> entry : blockList.entrySet()) {
                List<FallingBlock> blocks = entry.getValue();
                blocks.forEach(block -> {
                    if (block.getCustomName().contains(p.getName())) {
                        block.remove();
                    }
                });
            }
        }, seconds * 20);
    }

    public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        List<Block> blocks = new ArrayList<>();
        int bX = location.getBlockX(),
                bY = location.getBlockY(),
                bZ = location.getBlockZ();
        for (int x = bX - radius; x <= bX + radius; x++)
            for (int y = bY - radius; y <= bY + radius; y++)
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = ((bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z));
                    if (distance < radius * radius
                            && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
                        Location l = new Location(location.getWorld(), x, y, z);
                        if (l.getBlock().getType() != Material.BARRIER)
                            blocks.add(l.getBlock());
                    }
                }
        return blocks;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockChangeState(EntityChangeBlockEvent event) {
        Player p = null;
        if (event.getEntity().getCustomName() != null)
            p = Bukkit.getPlayer(event.getEntity().getCustomName());

        if (p != null && event.getEntity() instanceof FallingBlock && blockList.containsKey(p.getUniqueId()) && blockList.get(p.getUniqueId()).contains(event.getEntity())) {
            event.setCancelled(true);
            blockList.get(p.getUniqueId()).remove(event.getEntity());
            FallingBlock fb = (FallingBlock) event.getEntity();
            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(fb.getMaterial(), event.getBlock().getData()), 0, 0, 0, 0.4f, 50, p.getLocation(), 128);
            event.getEntity().remove();
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "HULK SMASH!",
                "",
                ChatColor.GRAY + "Right clicking a block with your",
                ChatColor.GRAY + "fist makes you smash the ground,",
                ChatColor.GRAY + "knocking back enemies."
        );
    }
}
