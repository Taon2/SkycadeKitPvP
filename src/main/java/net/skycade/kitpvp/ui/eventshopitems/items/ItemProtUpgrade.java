package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ItemProtUpgrade extends EventShopItem {

    private YamlConfiguration yaml;
    private EventShopManager eventShopManager;

    public ItemProtUpgrade(EventShopManager eventShopManager) {
        super(eventShopManager, "§4Protection Upgrade", new ItemStack(Material.DIAMOND_CHESTPLATE), 50, 300);
        this.eventShopManager = eventShopManager;
    }

    private static List<Enchantment> protEnchants;

    static {
        protEnchants = Arrays.asList(
                Enchantment.PROTECTION_ENVIRONMENTAL,
                Enchantment.PROTECTION_EXPLOSIONS,
                Enchantment.PROTECTION_FIRE,
                Enchantment.PROTECTION_PROJECTILE
        );
    }

    public void giveReward(Player p) {
        this.yaml = eventShopManager.getYaml();
        Inventory inv = p.getInventory();
        int randomSlot = getRandomNum(p);

        if (inv.getItem(randomSlot) != null) {
            Map<Enchantment, Integer> item = inv.getItem(randomSlot).getEnchantments();
            for (Enchantment enchant : protEnchants) {
                if (item.containsKey(enchant)) {
                    int level = item.get(enchant);
                    inv.getItem(randomSlot).addUnsafeEnchantment(enchant, level + 1);
                } else {
                    inv.getItem(randomSlot).addUnsafeEnchantment(enchant, 1);
                }
                long now = System.currentTimeMillis();
                yaml.set((p.getUniqueId() + "." + getName()), now);
                eventShopManager.setYaml(yaml);
                eventShopManager.save();
            }
        } else {
            giveReward(p);
        }
    }

    public void reapplyReward(Player p) {
        this.yaml = eventShopManager.getYaml();
        Inventory inv = p.getInventory();
        int slot = eventShopManager.getYaml().getInt((p.getUniqueId() + "." + "generatedArmorSlot"));
        if (inv.getItem(slot) != null) {
            Map<Enchantment, Integer> item = inv.getItem(slot).getEnchantments();
            for (Enchantment enchant : protEnchants) {
                if (item.containsKey(enchant)) {
                    int level = item.get(enchant);
                    inv.getItem(slot).addUnsafeEnchantment(enchant, level + 1);
                } else {
                    inv.getItem(slot).addUnsafeEnchantment(enchant, 1);
                }
            }
        }
    }

    private int getRandomNum(Player p) {
        int randomSlot = ThreadLocalRandom.current().nextInt(36, 39);
        yaml.set((p.getUniqueId() + "." + "generatedArmorSlot"), randomSlot);
        return randomSlot;
    }

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Upgrade the protections on one",
                ChatColor.WHITE + "piece of armor by one level.",
                ChatColor.GOLD + "Price: " + ChatColor.WHITE + "50 Tokens.",
                ChatColor.GOLD + "Duration: " + ChatColor.WHITE + "5 Minutes.", "",
                ChatColor.GRAY + "Click to buy this upgrade."
        );
    }
}
