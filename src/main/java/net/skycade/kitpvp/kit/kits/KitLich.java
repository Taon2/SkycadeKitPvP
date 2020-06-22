package net.skycade.kitpvp.kit.kits;

import com.sun.org.glassfish.external.statistics.Stats;
import net.skycade.SkycadeCombat.data.CombatData;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.nms.EntityUtil;
import net.skycade.kitpvp.nms.MiniArmyZombie;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static net.skycade.kitpvp.Messages.*;

public class KitLich extends Kit {

    private ItemStack helmet;
    private ItemStack phylactery;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int ghostCooldown = 30;
    private Map<UUID, List<MiniArmyZombie>> ghostList = new HashMap<>();

    private int blockRemoveSpeed = 90;
    private Map<UUID, Map<Location, BlockState>> placed = new HashMap<>();
    private List<UUID> removePhylactery = new ArrayList<>();

    public KitLich(KitManager kitManager) {
        super(kitManager, "Lich", KitType.LICH, 0, getLore());

        helmet = new ItemBuilder(
                Material.SKULL_ITEM)
                .setDurability((short) 1)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.fromRGB(25, 25, 112)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.fromRGB(25, 25, 112)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.fromRGB(25, 25, 112)).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Shift + Right clicking every " + ghostCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "summons ghosts to fight for you.").build();
        phylactery = new ItemBuilder(
                Material.BEACON)
                .setName(ChatColor.DARK_AQUA + "Phylactery")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Can be placed.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "If you die within 1.5 minutes of placing, you")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "respawn with half health at the placed location.").build();

        ItemStack icon = new ItemStack(
                Material.BEACON);
        setIcon(icon);

        EntityUtil.registerEntity(MiniArmyZombie.class, 54, "MiniArmyZombie");
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        if (!removePhylactery.contains(p.getUniqueId()))
            p.getInventory().addItem(phylactery);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.STONE_SWORD)
            return;
        if (!addCooldown(p, "Raise Undead", ghostCooldown, false))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        SUMMONED_GHOSTS.msg(p);

        // Spawn entities
        List<MiniArmyZombie> ghosts = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            // Can spawn 2 phylactery in any direction
            Location loc = p.getLocation().clone().add(ThreadLocalRandom.current().nextInt(-2, 3), 0, ThreadLocalRandom.current().nextInt(-2, 3));

            // Make sure valid Y value
            while (loc.getBlock().getType() != Material.AIR) {
                loc.add(0, 1, 0);

                // If y is greater than world height, break
                if (loc.getBlockY() >= loc.getWorld().getMaxHeight())
                    break;
            }

            // If y is greater than world height, cancel spawn
            if (loc.getBlockY() >= loc.getWorld().getMaxHeight()) {
                continue;
            }

            // Spawn entity
            MiniArmyZombie entity = EntityUtil.spawnCustomEntity(MiniArmyZombie.class, p.getLocation());

            if (entity == null) return;

            entity.setOwner(p);
            Zombie zombie = (Zombie) entity.getBukkitEntity();
            zombie.setCustomName(p.getName());

            // Add potion effects and items
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, false));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2, true, false)); // Some regeneration
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2, true, false)); // Some damage reduction

            EntityEquipment equipment = zombie.getEquipment();

            //Ghost head
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner("tnt");
            skull.setItemMeta(skullMeta);
            equipment.setHelmet(skull);

            //Grey chestplate
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            leatherArmorMeta.setColor(Color.GRAY);
            chestplate.setItemMeta(leatherArmorMeta);
            equipment.setChestplate(chestplate);

            //Stops armor from dropping
            equipment.setItemInHand(item.clone());
            equipment.setItemInHandDropChance(0.0F);
            equipment.setHelmetDropChance(0.0F);
            equipment.setChestplateDropChance(0.0F);

            zombie.getWorld().playEffect(zombie.getLocation(), Effect.SMOKE, 5);
            zombie.getWorld().playSound(zombie.getLocation(), Sound.ZOMBIE_IDLE, 1.0F, 1.0F);

            ghosts.add(entity);
        }

        ghostList.put(p.getUniqueId(), ghosts);
        removeSummon(15, p);
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            if (ghostList.containsKey(p.getUniqueId())) {
                for (MiniArmyZombie ghost : ghostList.get(p.getUniqueId())) {
                    ghost.getBukkitEntity().remove();
                }
                ghostList.remove(p.getUniqueId());
            }
        }, seconds * 20);
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() == EntityType.ZOMBIE && (((CraftEntity) event.getEntity()).getHandle() instanceof MiniArmyZombie)) {
            if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getName().contains(event.getDamager().getName())) {
                event.setCancelled(true);
            }
        }

        //Combat tags the player upon being hit by the ghosts
        if (event.getDamager().getType() == EntityType.ZOMBIE && (((CraftEntity) event.getDamager()).getHandle() instanceof MiniArmyZombie)) {
            MiniArmyZombie entity = (MiniArmyZombie) ((CraftEntity) event.getDamager()).getHandle();

            CombatData.Combat entityCombat = CombatData.getCombat(Bukkit.getPlayer(entity.getOwnerUUID()));

            if (event.getEntity().getType() == EntityType.PLAYER && event.getEntity() != entity.getOwner()) {
                entityCombat.setInCombat(true);
            }
        }
        //Combat tags the players upon hitting the ghosts
        else if (event.getEntity().getType() == EntityType.ZOMBIE && (((CraftEntity) event.getEntity()).getHandle() instanceof MiniArmyZombie)) {
            MiniArmyZombie entity = (MiniArmyZombie) ((CraftEntity) event.getEntity()).getHandle();

            CombatData.Combat playerCombat = CombatData.getCombat(Bukkit.getPlayer(entity.getOwnerUUID()));
            CombatData.Combat entityCombat = CombatData.getCombat((Player) event.getDamager());

            if (event.getDamager() == entity.getOwner())
                event.setCancelled(true);

            if (event.getDamager().getType() == EntityType.PLAYER && event.getDamager() != entity.getOwner()) {
                playerCombat.setInCombat(true);
                entityCombat.setInCombat(true);
            }
        }

        //Sets the target for the ghosts
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();

        if (ghostList.containsKey(damager.getUniqueId())) {
            for (MiniArmyZombie ghost : ghostList.get(damager.getUniqueId())) {
                ((Zombie) ghost.getBukkitEntity()).setTarget((LivingEntity) damagee);
            }
        } else if (ghostList.containsKey(damagee.getUniqueId())) {
            for (MiniArmyZombie ghost : ghostList.get(damagee.getUniqueId())) {
                ((Zombie) ghost.getBukkitEntity()).setTarget((LivingEntity) damager);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (event.getEntity() != null && event.getEntity().getKiller() != null && ghostList.containsKey(event.getEntity().getKiller().getUniqueId())) {
            ghostList.forEach((uuid, ghosts) -> {
                for (MiniArmyZombie ghost : ghosts) {
                    Zombie zombie = (Zombie) ghost.getBukkitEntity();
                    if (zombie.getTarget().getUniqueId().equals(entity.getUniqueId())) {
                        zombie.setTarget(null);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        KitPvPStats stats = KitPvP.getInstance().getStats(event.getPlayer());

        if (stats.getActiveKit() == KitType.LICH) {
            if (placed.containsKey(event.getPlayer().getUniqueId())) {
                placed.get(event.getPlayer().getUniqueId()).forEach((loc, replace) -> {
                    Material material = replace.getType();

                    loc.getBlock().setType(material);
                    BlockState blockState = loc.getBlock().getState();
                    blockState.setData(replace.getData());
                    blockState.update();
                });
                placed.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @Override
    public void onBlockPlace(Player p, Block block, BlockState replaced) {
        if (block.getType() != Material.BEACON)
            return;

        HashMap<Location, BlockState> replacedBlock = new HashMap<>();
        replacedBlock.put(block.getLocation(), replaced);
        placed.put(p.getUniqueId(), replacedBlock);
        PHYLACTERY_PLACED.msg(p);

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            placed.get(p.getUniqueId()).forEach((loc, replace) -> {
                Material material = replace.getType();

                if (loc.equals(block.getLocation())) {
                    loc.getBlock().setType(material);
                    BlockState blockState = loc.getBlock().getState();
                    blockState.setData(replace.getData());
                    blockState.update();
                    if (p.isOnline())
                        PHYLACTERY_EXPIRED.msg(p);
                }
            });
        }, blockRemoveSpeed * 20);
    }

    @Override
    public void onBlockBreak(Player p, Block block) {
        if (block.getType() != Material.BEACON)
            return;

        placed.forEach((uuid, replaceMap) -> {
            replaceMap.forEach((loc, replace) -> {
                Material material = replace.getType();
                BlockState state = replace;

                if (loc.equals(block.getLocation())) {
                    loc.getBlock().setType(material);
                    BlockState blockState = loc.getBlock().getState();
                    blockState.setData(state.getData());
                    blockState.update();

                    if (Bukkit.getOfflinePlayer(p.getUniqueId()).isOnline())
                        PHYLACTERY_BROKEN.msg(Bukkit.getPlayer(uuid), "%player%", p.getName());
                    YOU_BROKE_PHYLACTERY.msg(p, "%player%", Bukkit.getOfflinePlayer(uuid).getName());

                    placed.remove(uuid);
                }
            });
        });
    }


    @Override
    public boolean onDeath(Player died, Player killer) {
        if (!placed.containsKey(died.getUniqueId()))
            return true;

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> UtilPlayer.reset(died), 1);
        died.setHealth(died.getMaxHealth()/2);
        died.setVelocity(new Vector(0, 0, 0));
        died.setGameMode(GameMode.SURVIVAL);
        placed.get(died.getUniqueId()).forEach((loc, replace) -> {
            Material material = replace.getType();

            died.teleport(loc);
            loc.getBlock().setType(material);
            BlockState blockState = loc.getBlock().getState();
            blockState.setData(replace.getData());
            blockState.update();
        });
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), died::updateInventory, 10);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> died.setVelocity(new org.bukkit.util.Vector(0, 0, 0)), 5);
        KitPvPStats stats = KitPvP.getInstance().getStats(died);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            stats.getActiveKit().getKit().giveSoup(died, 32);
        }, 5);
        stats.applyKitPreference();
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            removePhylactery.add(died.getUniqueId());
            stats.getActiveKit().getKit().beginApplyKit(died);
            KitPvP.getInstance().getEventShopManager().reapplyUpgrades(died);
            removePhylactery.remove(died.getUniqueId());
        }, 3);


        PHYLACTERY_RESPAWNED.msg(died);
        placed.remove(died.getUniqueId());

        return false;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (placed.containsKey(event.getPlayer().getUniqueId())) {
            placed.get(event.getPlayer().getUniqueId()).forEach((loc, replace) -> {
                Material material = replace.getType();

                loc.getBlock().setType(material);
                BlockState blockState = loc.getBlock().getState();
                blockState.setData(replace.getData());
                blockState.update();
            });
            placed.remove(event.getPlayer().getUniqueId());
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Prestige to level 75!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Ruler of the undead.",
                "",
                ChatColor.GRAY + "Shift + Right clicking summons 2 ghosts",
                ChatColor.GRAY + "to attack enemies for you.",
                ChatColor.GRAY + "If you die within 1.5 minutes of placing your",
                ChatColor.GRAY + "phylactery, you respawn at the phylactery's location",
                ChatColor.GRAY + "with half health. Players can break your phylactery."
        );
    }
}
