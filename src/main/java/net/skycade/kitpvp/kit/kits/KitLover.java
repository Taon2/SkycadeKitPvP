package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitLover extends Kit {

    public KitLover(KitManager kitManager) {
        super(kitManager, "Lover", KitType.LOVER, 19000, "Love is a weird thing");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "RED_ROSE");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 19000);

        defaultsMap.put("inventory.sword.material", "WOOD_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 10);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 2);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 12);
        defaultsMap.put("armor.enchantments.protection", 4);

        setConfigDefaults(defaultsMap);

        if (getConfig().getString("kit.icon.material") != null) {
            if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
                setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
                        .setColour(getColor(getConfig().getString("kit.icon.color"))).build());
            } else {
                setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
            }
        } else {
            setIcon(new ItemStack(Material.DIRT));
        }
        setPrice(getConfig().getInt("kit.price"));
    }

    @Override
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.RED_ROSE).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.RED));
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.RED_ROSE)
            return;
        int level = getLevel(p);
        if (!addCooldown(p, getName(), 15, true))
            return;
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 3));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 200, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));

        ParticleEffect.HEART.display(0.5F, 0.5F, 0.5F, 1, 10, target.getLocation().add(0, 2, 0), 100);
        target.sendMessage("§cI LOVE YOU <3!");
    }

    @Override
    public List<String> getAbilityDesc() {
        return Collections.singletonList("§7Use the flower to love someone");
    }

}
