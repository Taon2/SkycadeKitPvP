package net.skycade.kitpvp.ui.eventshopitems.items;

import net.skycade.kitpvp.ui.eventshopitems.EventShopItem;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ItemPotionEffect extends EventShopItem {

    private static final List<PotionEffectType> potionEffects;

    static {
        potionEffects = Arrays.asList(
                PotionEffectType.DAMAGE_RESISTANCE,
                PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.REGENERATION,
                PotionEffectType.SPEED
        );
    }

    private YamlConfiguration yaml;
    private final EventShopManager eventShopManager;

    public ItemPotionEffect(EventShopManager eventShopManager) {
        super(eventShopManager, "Random Potion Upgrade", new ItemStack(Material.GLASS_BOTTLE), 10, 300, true);
        this.eventShopManager = eventShopManager;
    }

    public void giveReward(Player p) {
        this.yaml = eventShopManager.getYaml();
        int duration = getDuration() * 20;

        p.addPotionEffect(new PotionEffect(getRandomEffect(p), duration, 0));
        long now = System.currentTimeMillis();
        yaml.set((p.getUniqueId() + "." + getName()), now);
        eventShopManager.setYaml(yaml);
        eventShopManager.save();
    }

    public void reapplyReward(Player p) {
        this.yaml = eventShopManager.getYaml();
        int duration = (int) (getDuration() - ((System.currentTimeMillis() - yaml.getLong(p.getUniqueId().toString() + "." + getName())) / 1000L)) * 20;

        PotionEffectType type = potionEffects.get(yaml.getInt((p.getUniqueId() + "." + "generatedPotionNum")));

        p.addPotionEffect(new PotionEffect(type, duration, 0));
    }

    private PotionEffectType getRandomEffect(Player p) {
        this.yaml = eventShopManager.getYaml();
        int randomEffect = ThreadLocalRandom.current().nextInt(potionEffects.size());
        yaml.set((p.getUniqueId() + "." + "generatedPotionNum"), randomEffect);
        eventShopManager.setYaml(yaml);
        eventShopManager.save();
        return potionEffects.get(randomEffect);
    }

    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.GRAY + "",
                ChatColor.GRAY + "Receive a random beneficial",
                ChatColor.GRAY + "potion effect."
        );
    }

}
