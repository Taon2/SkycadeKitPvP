package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KitKnight extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    public KitKnight(KitManager kitManager) {
        super(kitManager, "Knight", KitType.KNIGHT, 26000, getLore());

        helmet = new ItemBuilder(
                Material.CHAINMAIL_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        chestplate = new ItemBuilder(
                Material.CHAINMAIL_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        leggings = new ItemBuilder(
                Material.CHAINMAIL_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        boots = new ItemBuilder(
                Material.CHAINMAIL_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();

        ItemStack icon = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
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

    //todo remove this and replace with thing from below
    @Override
    public void onMove(Player p) {
        for (Player target : UtilPlayer.getNearbyPlayers(p.getLocation(), 6)) {
            if (getKitManager().getKitPvP().getStats(target).getActiveKit() == KitType.GHOST) {
                Location location = target.getLocation();
                for (int i = 0; i < 30; i++) {
                    double angle, x, z;
                    angle = 2 * Math.PI * i / 30;
                    x = Math.cos(angle) * 1;
                    z = Math.sin(angle) * 1;
                    location.add(x, 0, z);
                    ParticleEffect.VILLAGER_HAPPY.display(0.03F, 0.02F, 0.03F, 0.05F, 1, location, Collections.singletonList(p));
                    location.subtract(x, 0, z);
                }
            }
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    //todo make this kit have an ability to simulate last line
    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.AQUA + "" + ChatColor.BOLD + "Defensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Protects his king.",
                "",
                ChatColor.GRAY + "Being in a gang with a player",
                ChatColor.GRAY + "using king kit increases your damage."
        );
    }
}
