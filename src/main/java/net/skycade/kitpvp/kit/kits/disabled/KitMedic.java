package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.HEALED;
import static net.skycade.kitpvp.Messages.PLAYER_HEALED;

public class KitMedic extends Kit {

    public KitMedic(KitManager kitManager) {
        super(kitManager, "Medic", KitType.MEDIC, 16000, false, "Helpful in battle");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "SHEARS");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 16000);

        defaultsMap.put("inventory.hoe.material", "IRON_HOE");
        defaultsMap.put("inventory.hoe.enchantments.durability", 5);
        defaultsMap.put("inventory.hoe.enchantments.damage-all", 5);

        defaultsMap.put("inventory.leather.amount", 5);
        defaultsMap.put("inventory.leather.max-amount", 5);
        defaultsMap.put("inventory.leather.lore1", "§cDrop to heal");
        defaultsMap.put("inventory.leather.lore2", "§cPlayers around you");
        defaultsMap.put("inventory.leather.name", "§cMedpack");
        defaultsMap.put("inventory.leather.interval", 8);

        defaultsMap.put("inventory.shears.lore", "§cCan be used to heal a player.");

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 12);
        defaultsMap.put("armor.enchantments.protection", 3);

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
    public void applyKit(Player p) {
        p.getInventory().addItem(new ItemBuilder(
                Material.getMaterial(getConfig().getString("inventory.hoe.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.hoe.enchantments.durability"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.hoe.enchantments.damage-all")).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.LEATHER, getConfig().getInt("inventory.leather.amount"))
                .addLore(Arrays.asList(
                        getConfig().getString("inventory.leather.lore1"),
                        getConfig().getString("inventory.leather.lore2")))
                .setName(getConfig().getString("inventory.leather.name")).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.SHEARS)
                .addLore(getConfig().getString("inventory.shears.lore")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.RED));

        startItemRunnable(p, getConfig().getInt("inventory.leather.interval"), new ItemBuilder(
                        Material.LEATHER, getConfig().getInt("inventory.leather.amount"))
                        .addLore(Arrays.asList(
                                getConfig().getString("inventory.leather.lore1"),
                                getConfig().getString("inventory.leather.lore2")))
                        .setName(getConfig().getString("inventory.leather.name")).build(),
                getConfig().getInt("inventory.leather.max-amount"), KitType.MEDIC);
    }

    public void onMedpackUse(Player p, Item medpack) {
        Set<Player> players = UtilPlayer.getNearbyPlayers(p.getLocation(), 3);
        players.remove(p);

        players.forEach((player) -> {
            player.setHealth(player.getMaxHealth());
            HEALED.msg(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
            PLAYER_HEALED.msg(p, "%player%", player.getName());
        });
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), medpack::remove, 8);
    }

    public void onMove(Player p) {
        if (p.getItemInHand().getType().equals(Material.LEATHER)) {
            particleMoveEffect(p, ParticleEffect.CRIT, 3, 16);
        }
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.SHEARS)
            return;
        if (!addCooldown(p, "Shears", 4, false))
            return;
        target.setHealth(target.getMaxHealth());
        PLAYER_HEALED.msg(p, "%player%", target.getName());
        HEALED.msg(target);
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Throw down medpacks to heal", "§7players around you and", "§7use the shears to heal someone");
    }

}
