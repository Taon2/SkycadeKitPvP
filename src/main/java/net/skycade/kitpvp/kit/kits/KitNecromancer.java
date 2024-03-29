package net.skycade.kitpvp.kit.kits;

import net.skycade.SkycadeCombat.data.CombatData;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.nms.EntityUtil;
import net.skycade.kitpvp.nms.MiniArmyZombie;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static net.skycade.kitpvp.Messages.*;

public class KitNecromancer extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack snowball;

    private int ghostCooldown = 25;

    private int snowballCooldown = 7;
    private int snowballStartAmount = 6;
    private int snowballMaxAmount = 8;
    private int snowballRegenSpeed = 20;

    private List<Snowball> snowballList = new ArrayList<>();

    private Map<UUID, List<MiniArmyZombie>> ghostList = new HashMap<>();

    public KitNecromancer(KitManager kitManager) {
        super(kitManager, "Necromancer", KitType.NECROMANCER, 40000, getLore());

        helmet = new ItemBuilder(
                Material.SKULL_ITEM)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 11)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.GRAY).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 11)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.GRAY).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY,  11)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .setColour(Color.GRAY).build();
        weapon = new ItemBuilder(
                Material.BONE)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% every " + ghostCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "summons ghosts to fight for you.").build();
        snowball = new ItemBuilder(
                Material.SNOW_BALL, snowballStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Throwing a snowball every " + snowballCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "gives your target withering and teleports")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "your ghosts to your enemies location.")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 snowball every " + snowballRegenSpeed + " seconds.").build();


        ItemStack icon = new ItemStack(
                Material.SKULL_ITEM);
        setIcon(icon);

        EntityUtil.registerEntity(MiniArmyZombie.class, 54, "MiniArmyZombie");
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(snowball);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, snowballRegenSpeed, getSnowball(1), snowballMaxAmount, KitType.NECROMANCER);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.BONE)
            return;
        if (!addCooldown(p, "Summon Ghosts", ghostCooldown, false))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        SUMMONED_GHOSTS.msg(p);

        // Spawn entities
        List<MiniArmyZombie> ghosts = new ArrayList<>();

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

        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (Snowball snowball : snowballList)
                if (snowball.getCustomName().contains(p.getName())) {
                    snowball.remove();
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

            CombatData.Combat playerCombat = CombatData.getCombat((Player) event.getEntity());
            CombatData.Combat playerTwoCombat = CombatData.getCombat(Bukkit.getPlayer(entity.getOwnerUUID()));

            if (event.getEntity().getType() == EntityType.PLAYER && event.getEntity() != entity.getOwner()) {
                playerCombat.setInCombat(true);
                playerTwoCombat.setInCombat(true);
            }
        }
        //Combat tags the players upon hitting the ghosts
        else if (event.getEntity().getType() == EntityType.ZOMBIE && (((CraftEntity) event.getEntity()).getHandle() instanceof MiniArmyZombie)) {
            MiniArmyZombie entity = (MiniArmyZombie) ((CraftEntity) event.getEntity()).getHandle();

            CombatData.Combat playerCombat = CombatData.getCombat(Bukkit.getPlayer(entity.getOwnerUUID()));
            CombatData.Combat playerTwoCombat = CombatData.getCombat((Player) event.getDamager());

            if (event.getDamager() == entity.getOwner())
                event.setCancelled(true);

            if (event.getDamager().getType() == EntityType.PLAYER && event.getDamager() != entity.getOwner()) {
                playerCombat.setInCombat(true);
                playerTwoCombat.setInCombat(true);
            }
        }

        //Sets the target for the ghosts
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();

        if (damager instanceof LivingEntity && damagee instanceof LivingEntity) {
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
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        if (ghostList.containsKey(event.getEntity().getKiller().getUniqueId())) {
            ghostList.forEach((uuid, ghosts) -> {
                for (MiniArmyZombie ghost : ghosts) {
                    Zombie zombie = (Zombie) ghost.getBukkitEntity();
                    if (zombie.getTarget().getUniqueId().equals(event.getEntity().getUniqueId())) {
                        zombie.setTarget(null);
                    }
                }
            });
        }
    }

    public void onSnowballUse(Player shooter, ProjectileLaunchEvent e) {
        e.getEntity().setCustomName(shooter.getName());
        e.getEntity().setCustomNameVisible(false);
        snowballList.add((Snowball) e.getEntity());

        e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(2.5D));
    }

    public void onSnowballHit(Player shooter, Player damagee) {
        if (CaptureTheFlagEvent.getInstance().getBegin() != null && CaptureTheFlagEvent.getInstance().isTeamRed(shooter) == CaptureTheFlagEvent.getInstance().isTeamRed(damagee)) {
            return;
        }

        if (!addCooldown(shooter, "Curse", snowballCooldown, true)) {
            return;
        }

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(shooter, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        YOURE_WITHERED.msg(damagee, "%player%", shooter.getName());
        damagee.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 80, 4));

        if (ghostList.containsKey(shooter.getUniqueId())) {
            for (MiniArmyZombie ghost : ghostList.get(shooter.getUniqueId())) {
                ((Zombie) ghost.getBukkitEntity()).teleport(damagee.getLocation());
                ((Zombie) ghost.getBukkitEntity()).setTarget(damagee);
            }

            TELEPORTED_GHOSTS.msg(shooter, "%player%", damagee.getName());
        }
    }

    @Override
    public void reimburseItem(Player p, ItemStack item) {
        int count = -1;
        for (ItemStack itemStack : p.getInventory()) {
            if (itemStack != null && item != null && item.getType() == itemStack.getType() && item.getDurability() == itemStack.getDurability()) {
                count += itemStack.getAmount();
            }
        }

        if (item != null && item.getType() == getSnowball(item.getAmount()).getType() && count < snowballMaxAmount) {
            Inventory inv = p.getInventory();
            int amount = 0;
            ItemStack newItem = getSnowball(1);

            Integer finalSlot = null;
            for (Integer i = 0; i < inv.getSize(); i++)
                if (inv.getItem(i) != null)
                    if (inv.getItem(i).getType() == newItem.getType()) {
                        amount += inv.getItem(i).getAmount();
                        if (amount <= inv.getMaxStackSize())
                            finalSlot = i;
                    }
            if (finalSlot != null && amount > 0) {
                ItemStack invItem = inv.getItem(finalSlot);
                if (amount < snowballMaxAmount)
                    inv.setItem(finalSlot, new ItemStack(invItem.getType(), invItem.getAmount() + 1));
            } else
                p.getInventory().addItem(newItem);
        }
    }

    private ItemStack getSnowball(int amount) {
        ItemStack snowballRegen = new ItemStack(snowball);
        snowballRegen.setAmount(amount);

        return snowballRegen;
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
                ChatColor.GRAY + "%click% your sword",
                ChatColor.GRAY + "summons ghosts to fight for you.",
                ChatColor.GRAY + "Throw snowballs to wither enemies",
                ChatColor.GRAY + "and make your ghosts target that enemy."
        );
    }
}
