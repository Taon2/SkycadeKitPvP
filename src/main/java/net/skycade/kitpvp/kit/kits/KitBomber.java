package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class KitBomber extends Kit {

    public KitBomber(KitManager kitManager) {
        super(kitManager, "Bomber", KitType.BOMBER, 7000, "Bombs away!");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "TNT");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 7000);

        defaultsMap.put("inventory.sword.material", "IRON_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 0);
        defaultsMap.put("inventory.tnt.amount", 10);
        defaultsMap.put("inventory.tnt.regen-amount", 1);
        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.helmet.level", 1);
        defaultsMap.put("armor.helmet.enchantments.durability", 12);
        defaultsMap.put("armor.helmet2.material", "LEATHER");
        defaultsMap.put("armor.helmet2.enchantments.durability", 10);
        defaultsMap.put("armor.helmet2.enchantments.protection", 1);
        defaultsMap.put("armor.enchantments.explosion-protection", 5);

        defaultsMap.put("potions.pot1", "SPEED:1");

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

        p.getInventory().addItem(getTnt(p,
                getConfig().getInt("inventory.tnt.amount"),
                getConfig().getInt("inventory.tnt.regen-amount")));

        ItemStack[] armor = getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.helmet.enchantments.durability"),
                getConfig().getInt("armor.helmet.level"),
                Color.RED);

        armor[3] = new ItemBuilder(
                Material.getMaterial(getConfig().getString("armor.helmet2.material").toUpperCase() + "_HELMET"))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet2.enchantments.durability"))
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet2.enchantments.protection"))
                .setColour(Color.WHITE).build();

        for (ItemStack piece : armor)
            piece.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, getConfig().getInt("armor.enchantments.explosion-protection"));

        p.getInventory().setArmorContents(armor);

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));

        startItemRunnable(p, 20 - (level * 5), getTnt(p, 1, level), 10, KitType.BOMBER);
    }

    private ItemStack getTnt(Player p, int amount, int level) {
        return new ItemBuilder(Material.TNT, amount).setName("§bTNT").addLore("§F" + "Regain 1 tnt every " + (20 - (level * 5)) + " seconds").build();
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.TNT)
            return;
        if (!addCooldown(p, getName(), 6, true)) return;
        Location loc = p.getEyeLocation();
        TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.PRIMED_TNT);
        tnt.setVelocity(loc.getDirection().multiply(1D));
        tnt.setCustomName(p.getName());
        tnt.setFuseTicks(30);

        p.getWorld().playEffect(p.getLocation(), Effect.CLICK1, 1);
        if (p.getInventory().getItemInHand().getAmount() - 1 >= 1)
            p.getInventory().setItemInHand(getTnt(p, p.getInventory().getItemInHand().getAmount() - 1, getLevel(p)));
        else
            p.getInventory().remove(p.getItemInHand());
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7You can fire tnt", "§7you will regain tnt overtime.");
    }

}
