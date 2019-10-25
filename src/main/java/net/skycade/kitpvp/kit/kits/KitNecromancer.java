package net.skycade.kitpvp.kit.kits;

import net.minelink.ctplus.CombatTagPlus;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.nms.EntityUtil;
import net.skycade.kitpvp.nms.MiniArmyZombie;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.skycade.kitpvp.Messages.SUMMONED_GHOSTS;

public class KitNecromancer extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private List<MiniArmyZombie> ghostList = new ArrayList<>();

    public KitNecromancer(KitManager kitManager) {
        super(kitManager, "Necromancer", KitType.NECROMANCER, 40000, getLore());

        helmet = new ItemBuilder(
                Material.SKULL_ITEM)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.GRAY).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.GRAY).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.GRAY).build();
        weapon = new ItemBuilder(
                Material.BONE)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5).build();

        ItemStack icon = new ItemStack(
                Material.SKULL_ITEM);
        setIcon(icon);

        EntityUtil.registerEntity(MiniArmyZombie.class, 54, "MiniArmyZombie");
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
        if (item.getType() != Material.BONE)
            return;
        if (!addCooldown(p, "summon ghosts", 20, false))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        SUMMONED_GHOSTS.msg(p);

        // Spawn entities
        for (int i = 0; i < 2; i++) {
            // Can spawn 2 blocks in any direction
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
            zombie.setCustomName(p.getName() + "'s" + " Ghost");

            // Ensure invulnerable
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2, true, false)); // Some regeneration
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2, true, false)); // Some damage reduction

            // Add potion effects and items
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, true, false));

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

            ghostList.add(entity);
        }
        removeSummon(20, p);
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (MiniArmyZombie ghost : ghostList)
                if (ghost.getCustomName().contains(p.getName())) {
                    ghost.getBukkitEntity().remove();
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
        CombatTagPlus pl = (CombatTagPlus) Bukkit.getPluginManager().getPlugin("CombatTagPlus");

        //Combat tags the player upon being hit by the ghosts
        if (event.getDamager().getType() == EntityType.ZOMBIE && (((CraftEntity) event.getDamager()).getHandle() instanceof MiniArmyZombie)) {
            MiniArmyZombie entity = (MiniArmyZombie) ((CraftEntity) event.getDamager()).getHandle();

            if (event.getEntity().getType() == EntityType.PLAYER && event.getEntity() != entity.getOwner()) {
                pl.getTagManager().tag((Player) event.getEntity(), Bukkit.getPlayer(entity.getOwnerUUID()));
            }
        }
        //Combat tags the players upon hitting the ghosts
        else if (event.getEntity().getType() == EntityType.ZOMBIE && (((CraftEntity) event.getEntity()).getHandle() instanceof MiniArmyZombie)) {
            MiniArmyZombie entity = (MiniArmyZombie) ((CraftEntity) event.getEntity()).getHandle();

            if (event.getDamager() == entity.getOwner())
                event.setCancelled(true);

            if (event.getDamager().getType() == EntityType.PLAYER && event.getDamager() != entity.getOwner()) {
                pl.getTagManager().tag(Bukkit.getPlayer(entity.getOwnerUUID()), (Player) event.getDamager());
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity().getType() != EntityType.ZOMBIE && !(((CraftEntity) event.getEntity()).getHandle() instanceof MiniArmyZombie))
            return;

        event.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.ZOMBIE && !(((CraftEntity) event.getEntity()).getHandle() instanceof MiniArmyZombie))
            return;

        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "A healer with bad timing.",
                "",
                ChatColor.GRAY + "Right clicking your sword",
                ChatColor.GRAY + "summons ghosts to fight for you."
        );
    }
}
