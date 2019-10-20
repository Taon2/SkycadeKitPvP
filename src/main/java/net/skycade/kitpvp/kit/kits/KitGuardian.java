//package net.skycade.kitpvp.kit.kits;
//
//import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
//import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
//import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
//import net.skycade.kitpvp.kit.Kit;
//import net.skycade.kitpvp.kit.KitManager;
//import net.skycade.kitpvp.kit.KitType;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Color;
//import org.bukkit.Material;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.entity.*;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class KitGuardian extends Kit {
//
//    ItemStack helmet;
//    ItemStack chestplate;
//    ItemStack leggings;
//    ItemStack boots;
//    ItemStack weapon;
//
//    public KitGuardian(KitManager kitManager) {
//        super(kitManager, "Guardian", KitType.GUARIDAN, 55000, getLore());
//
//        Map<String, Object> defaultsMap = new HashMap<>();
//
//        defaultsMap.put("kit.icon.material", "PRISMARINE_CRYSTALS");
//        defaultsMap.put("kit.icon.color", "BLACK");
//        defaultsMap.put("kit.price", 55000);
//
//        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
//        defaultsMap.put("inventory.sword.enchantments.durability", 5);
//        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);
//
//        defaultsMap.put("armor.material", "LEATHER");
//        defaultsMap.put("armor.enchantments.durability", 10);
//        defaultsMap.put("armor.enchantments.protection", 0);
//        defaultsMap.put("armor.enchantments.thorns", 1);
//
//        defaultsMap.put("armor.chestplate.material", "DIAMOND");
//        defaultsMap.put("armor.chestplate.enchantments.durability", 0);
//        defaultsMap.put("armor.chestplate.enchantments.thorns", 1);
//
//        setConfigDefaults(defaultsMap);
//
//        if (getConfig().getString("kit.icon.material") != null) {
//            if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
//                setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
//                        .setColour(getColor(getConfig().getString("kit.icon.color"))).build());
//            } else {
//                setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
//            }
//        } else {
//            setIcon(new ItemStack(Material.DIRT));
//        }
//        setPrice(getConfig().getInt("kit.price"));
//    }
//
//    @Override
//    public void applyKit(Player p) {
//        p.getInventory().addItem(new ItemBuilder(
//                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
//                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
//                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());
//
//        ItemStack[] armor = getArmour(
//                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
//                getConfig().getInt("armor.helmet.enchantments.durability"),
//                getConfig().getInt("armor.helmet.level"),
//                Color.fromRGB(0, 204, 204));
//
//        armor[3] = new ItemBuilder(
//                Material.getMaterial(getConfig().getString("armor.helmet2.material").toUpperCase() + "_HELMET"))
//                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet2.enchantments.durability"))
//                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet2.enchantments.protection"))
//                .setColour(Color.fromRGB(204, 102, 0)).build();
//
//        p.getInventory().setArmorContents(armor);
//
//        p.getInventory().setHelmet(new ItemBuilder(
//                Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
//                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability")).build());
//    }
//
//    @Override
//    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
//        List<Entity> entities = damagee.getNearbyEntities(4, 4, 4).stream()
//                .filter(en -> en instanceof Golem && en.getCustomName().contains(damagee.getName()))
//                .collect(Collectors.toList());
//        entities.forEach(golem -> ((Golem) golem).setTarget(damager));
//    }
//
//    @Override
//    public void onItemUse(Player p, ItemStack item) {
//        if (item.getType() != Material.IRON_SWORD)
//            return;
//        if (!addCooldown(p, getName(), 4 * 10, true))
//            return;
//
//        //For missions
//        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
//        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);
//
//        IronGolem golem = (IronGolem) p.getWorld().spawnEntity(p.getLocation(), EntityType.IRON_GOLEM);
//        golem.setCustomName(p.getName() + " golem");
//
//        Set<Player> nearbyPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 5);
//        nearbyPlayers.remove(p);
//
//        if (!(nearbyPlayers.isEmpty()))
//            golem.setTarget(getClosestTarget(nearbyPlayers, p));
//
//        removeSummon(30, p);
//    }
//
//    private Player getClosestTarget(Set<Player> players, Player p) {
//        double distance = 100;
//        Player closestPlayer = null;
//
//        for (Player target : players)
//            if (p.getLocation().distance(target.getLocation()) < distance)
//                closestPlayer = target;
//        return closestPlayer;
//    }
//
//    @Override
//    public List<String> getHowToObtain() {
//        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Prestige to level 75!");
//    }
//
//    public static List<String> getLore() {
//        return Arrays.asList(
//                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
//                ChatColor.GRAY + "" + ChatColor.ITALIC + "Plenty of fish in the sea.",
//                "",
//                ChatColor.GRAY + "Constantly fires a guardian beam at the closest enemy.",
//                ChatColor.GRAY + "Right clicking gives players within 10 blocks slowness."
//        );
//    }
//}
