package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

import static net.skycade.kitpvp.Messages.CANT_USE_HERE;

public class KitGolem extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int smashCooldown = 25;

    private Map<UUID, List<FallingBlock>> blockList = new HashMap<>();

    public KitGolem(KitManager kitManager) {
        super(kitManager, "Golem", KitType.GOLEM, 37000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.WOOD_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.DAMAGE_ALL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + smashCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "smashes the ground around you.").build();

        ItemStack icon = new ItemStack(Material.IRON_BLOCK);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.WOOD_SWORD)
            return;

        if (!addCooldown(p, "Ground Pound", smashCooldown, true))
            return;
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
                                    ((Player) ent).damage(4, p);
                                    ent.setVelocity(ent.getLocation().getDirection().multiply(-1.35F).setY(0.8F));
                                });
                    });
                }
            }
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
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Ground pound!",
                "",
                ChatColor.GRAY + "Smashes the ground in front of you."
        );
    }
}
