package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitBomber extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack tnt;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int tntStartAmount = 10;
    private int tntMaxAmount = 10;
    private int tntRegenSpeed = 15;

    public KitBomber(KitManager kitManager) {
        super(kitManager, "Bomber", KitType.BOMBER, 8000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.WHITE).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.RED).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.RED).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5)
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        tnt = new ItemBuilder(
                Material.TNT, tntStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 tnt every " + tntRegenSpeed + " seconds.").build();

        constantEffects.put(PotionEffectType.SPEED, 1);

        ItemStack icon = new ItemStack(Material.TNT);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(tnt);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });

        //todo make all runnables like this into the guardian ones, to save on memory. dont want to run stuff that isnt necessary
        startItemRunnable(p, tntRegenSpeed, getTnt(1), tntMaxAmount, KitType.BOMBER);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.TNT)
            return;
        if (!addCooldown(p, getName(), 6, true)) return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Location loc = p.getEyeLocation();
        TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.PRIMED_TNT);
        tnt.setVelocity(loc.getDirection().multiply(1D));
        tnt.setCustomName(p.getName());
        tnt.setFuseTicks(30);

        p.getWorld().playEffect(p.getLocation(), Effect.CLICK1, 1);
        if (p.getInventory().getItemInHand().getAmount() - 1 >= 1)
            p.getInventory().setItemInHand(getTnt(p.getInventory().getItemInHand().getAmount() - 1));
        else
            p.getInventory().remove(p.getItemInHand());
    }

    private ItemStack getTnt(int amount) {
        ItemStack tntRegen = new ItemStack(tnt);
        tntRegen.setAmount(amount);

        return tntRegen;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Bombs away!",
                "",
                ChatColor.GRAY + "Toss bombs at your enemies."
        );
    }
}
