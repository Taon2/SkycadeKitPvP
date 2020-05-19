package net.skycade.kitpvp.kit.kits;

import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.skycade.kitpvp.Messages.*;

public class KitBlockhunt extends Kit {

    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack bow;
    private ItemStack arrows;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private List<UUID> disguising = new ArrayList<>();
    private Map<UUID, EntityFallingBlock> blockEntites = new HashMap<>();
    private Map<UUID, Block> disguised = new HashMap<>();

    private int disguiseCooldown = 25;
    private int arrowRegenSpeed = 3;
    private int arrowStartAmount = 10;
    private int arrowMaxAmount = 12;

    public KitBlockhunt(KitManager kitManager) {
        super(kitManager, "Blockhunt", KitType.BLOCKHUNT, 25000, getLore());

        boots = new ItemBuilder(
                Material.DIAMOND_BOOTS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.DURABILITY, 2).build();
        weapon = new ItemBuilder(
                Material.IRON_AXE)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% on a block every " + disguiseCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "disguises you as that block.").build();
        bow = new ItemBuilder(
                Material.BOW)
                .addEnchantment(Enchantment.DURABILITY, 6)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1).build();
        arrows = new ItemBuilder(
                Material.ARROW, arrowStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 arrow every " + arrowRegenSpeed + " seconds.").build();

        constantEffects.put(PotionEffectType.SPEED, 0);
        constantEffects.put(PotionEffectType.DAMAGE_RESISTANCE, 1);

        ItemStack icon = new ItemStack(Material.WORKBENCH);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(bow);
        p.getInventory().setItem(27, arrows);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });

        startItemRunnable(p, arrowRegenSpeed, getArrows(1), arrowMaxAmount, KitType.BLOCKHUNT);
    }

    @Override
    public void onItemUse(Player p, ItemStack item, Block clickedBlock) {
        if (item.getType() != Material.IRON_AXE)
            return;
        if (disguised.containsKey(p.getUniqueId()))
            return;
        if (!addCooldown(p, "Block Disguise", disguiseCooldown, true))
            return;
        if (p.getLocation().getBlock().getType() != Material.AIR)
            return;
        if (clickedBlock.getType() == Material.FENCE
                || clickedBlock.getType() == Material.BARRIER
                || clickedBlock.getType() == Material.BANNER)
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Material disguiseType = clickedBlock.getType();
        byte disguiseData = clickedBlock.getData();

        disguising.add(p.getUniqueId());
        DISGUISING.msg(p);

        //Cool particles
        for (int i = 0; i < 2; i++) {
            Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), new BukkitRunnable() {
                @Override
                public void run() {
                    p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, disguiseType);
                }
            }, i * 20);
        }

        //Hides the player as a block
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                if (p.getLocation().getBlock().getType() == Material.AIR && disguising.contains(p.getUniqueId())) {
                    //Sends solid block to all players
                    Location loc = p.getLocation();
                    p.teleport(new Location(loc.getWorld(), Math.floor(loc.getX()) + .5, loc.getY(), Math.floor(loc.getZ()) + .5, loc.getYaw(), loc.getPitch()));

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (p.getUniqueId() != player.getUniqueId())
                            player.sendBlockChange(loc, disguiseType, disguiseData);
                    });

                    //Updates the fallingblock entity bound to the hider
                    addFallingBlock(p, disguiseType, disguiseData);

                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 3));
                    p.setCustomNameVisible(false);
                    disguised.put(p.getUniqueId(), loc.getBlock());
                    DISGUISED.msg(p);
                }
            }
        }, 40L);
    }

    @Override
    public void onMove(Player p) {
        removeDisguise(p);
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        removeDisguise(damager);
        removeDisguise(damagee);
    }

    @Override
    public boolean onDeath(Player died, Player killer) {
        removeDisguise(died);
        return true;
    }

    private void addFallingBlock(Player p, Material material, byte data) {
        removeFallingBlock(p);

        Location loc = p.getLocation();
        IBlockData blockdata = net.minecraft.server.v1_8_R3.Block.getByCombinedId(material.getId() + (data << 12));
        EntityFallingBlock entityfallingblock = new EntityFallingBlock(((CraftWorld) loc.getWorld()).getHandle(), loc.getX(), loc.getY(), loc.getZ(), blockdata);
        entityfallingblock.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        PacketPlayOutSpawnEntity packetSpawn = new PacketPlayOutSpawnEntity(entityfallingblock, 70, net.minecraft.server.v1_8_R3.Block.getCombinedId(entityfallingblock.getBlock()));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetSpawn);
        blockEntites.put(p.getUniqueId(), entityfallingblock);

        //Some neat particles
        loc.getWorld().playEffect(loc, Effect.STEP_SOUND, material);
    }

    private void removeFallingBlock(Player p) {
        //Removes the fallingblock entity from player
        if (blockEntites.containsKey(p.getUniqueId())) {
            PacketPlayOutEntityDestroy packetDestroy = new PacketPlayOutEntityDestroy(blockEntites.get(p.getUniqueId()).getId());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetDestroy);
        }
    }

    private void removeDisguise(Player p) {
        disguising.remove(p.getUniqueId());

        if (disguised.containsKey(p.getUniqueId())) {
            Block block = disguised.get(p.getUniqueId());
            Material type = block.getType();
            block.setType(Material.AIR);

            //Removes the solid block from all player's view
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendBlockChange(p.getLocation(), block.getType(), block.getData());
            });

            //Removes the fallingblock from the hidden player's view
            removeFallingBlock(p);

            p.getLocation().getWorld().playEffect(p.getLocation(), Effect.STEP_SOUND, type);
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
            disguised.remove(p.getUniqueId());
            p.setCustomNameVisible(true);
            DISGUISE_REMOVED.msg(p);
        }
    }

    private ItemStack getArrows(int amount) {
        ItemStack arrowRegen = new ItemStack(arrows);
        arrowRegen.setAmount(amount);

        return arrowRegen;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Hide and go seek!",
                "",
                ChatColor.GRAY + "Shift right click on a block",
                ChatColor.GRAY + "disguises you as that block.",
                ChatColor.GRAY + "Moving, attacking, or taking",
                ChatColor.GRAY + "damage removes this disguise."
        );
    }
}
