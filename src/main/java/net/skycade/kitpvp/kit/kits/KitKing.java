package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class KitKing extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int golemCooldown = 35;
    private List<IronGolem> golemList = new ArrayList<>();

    public KitKing(KitManager kitManager) {
        super(kitManager, "King", KitType.KING, 38000, getLore());

        helmet = new ItemBuilder(
                Material.GOLD_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 2).build();
        chestplate = new ItemBuilder(
                Material.IRON_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.IRON_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + golemCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "summons a golem to fight for you.").build();

        ItemStack icon = new ItemStack(Material.GOLD_HELMET);
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
        if (item.getType() != Material.IRON_SWORD)
            return;
        if (!addCooldown(p, "Summon Golem", golemCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        IronGolem golem = (IronGolem) p.getWorld().spawnEntity(p.getLocation(), EntityType.IRON_GOLEM);
        golem.setCustomName(p.getName() + " golem");
        golem.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));

        Set<Player> nearbyPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 5);
        nearbyPlayers.remove(p);

        if (!(nearbyPlayers.isEmpty()))
            golem.setTarget(getClosestTarget(nearbyPlayers, p));

        golemList.add(golem);

        removeSummon(30, p);
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            for (IronGolem golem : golemList)
                if (golem.getCustomName().contains(p.getName())) {
                    golem.remove();
                }
        }, seconds * 20);
    }

    @Override
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        List<Entity> entities = damagee.getNearbyEntities(4, 4, 4).stream()
                .filter(en -> en instanceof Golem && en.getCustomName().contains(damagee.getName()))
                .collect(Collectors.toList());
        entities.forEach(golem -> ((Golem) golem).setTarget(damager));
    }

    private Player getClosestTarget(Set<Player> players, Player p) {
        double distance = 100;
        Player closestPlayer = null;

        for (Player target : players)
            if (p.getLocation().distance(target.getLocation()) < distance)
                closestPlayer = target;
        return closestPlayer;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Bow down.",
                "",
                ChatColor.GRAY + "Right clicking spawns a golem",
                ChatColor.GRAY + "to attack enemies for you."
        );
    }
}
