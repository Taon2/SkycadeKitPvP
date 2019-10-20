package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.HEALED;

public class KitJesus extends Kit {

    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack book;

    public KitJesus(KitManager kitManager) {
        super(kitManager, "Jesus", KitType.JESUS, 30000, getLore());

        chestplate = new ItemBuilder(
                Material.DIAMOND_CHESTPLATE).build();
        leggings = new ItemBuilder(
                Material.DIAMOND_LEGGINGS).build();
        boots = new ItemBuilder(
                Material.DIAMOND_BOOTS)
                .addEnchantment(Enchantment.DEPTH_STRIDER, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Receive 50% more damage.").build();
        weapon = new ItemBuilder(
                Material.IRON_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        book = new ItemStack(
                Material.ENCHANTED_BOOK);

        ItemStack icon = new ItemStack(Material.ENCHANTED_BOOK);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(book);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        e.setDamage(e.getDamage() * 1.5);
    }

    @Override
    public void onMove(Player p) {
        if (p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
            if (!p.hasPotionEffect(PotionEffectType.SPEED))
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (3 * 3), 1));
        }
        particleTracerEffect(p, Color.BLUE, 20);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.ENCHANTED_BOOK)
            return;
        if (!addCooldown(p, getName(), 30, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        //todo gangs.forEach(). Send message saying Healed
        p.addPotionEffects(Collections.singletonList(new PotionEffect(PotionEffectType.REGENERATION, 60, 1)));
        HEALED.msg(p);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    //todo edit this kit and give it a regen heal ability
    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GREEN + "" + ChatColor.BOLD + "Support Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Respawns in 3 days. Just kidding.",
                "",
                ChatColor.GRAY + "Fast in water and",
                ChatColor.GRAY + "heals your friends.",
                ChatColor.GRAY + "Takes 50% extra damage when hit."
        );
    }
}
