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
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ItemProtUpgrade extends EventShopItem {

    private static final List<Enchantment> protEnchants;

    static {
        protEnchants = Arrays.asList(
                Enchantment.PROTECTION_ENVIRONMENTAL,
                Enchantment.PROTECTION_EXPLOSIONS,
                Enchantment.PROTECTION_FIRE,
                Enchantment.PROTECTION_PROJECTILE
        );
    }

    private YamlConfiguration yaml;
    private final EventShopManager eventShopManager;

    public ItemProtUpgrade(EventShopManager eventShopManager) {
        super(eventShopManager, "Protection Upgrade", new ItemStack(Material.DIAMOND_CHESTPLATE), 25, 300, true);
        this.eventShopManager = eventShopManager;
    }

    public void giveReward(Player p) {
        this.yaml = eventShopManager.getYaml();
        PlayerInventory inv = p.getInventory();
        int randomSlot = getRandomNum(p);

        if (inv.getArmorContents().length == 0) return;

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
                ChatColor.GRAY + "",
                ChatColor.GRAY + "Upgrade the protections on one",
                ChatColor.GRAY + "piece of armor by one level."
        );
    }
}
