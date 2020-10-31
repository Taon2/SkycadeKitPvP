package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitSorcerer extends Kit {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int absorptionDuration = 5;
    private int absorptionChance = 25;

    private int abiltiyCooldown = 25;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitSorcerer(KitManager kitManager){
        super(kitManager, "Sorcerer", KitType.SORCERER, 20000, getLore());

        helmet = new ItemBuilder(Material.STAINED_GLASS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .build();

        chestplate = new ItemBuilder(Material.GOLD_CHESTPLATE)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .build();

        leggings = new ItemBuilder(Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .setColour(Color.PURPLE)
                .build();

        boots = new ItemBuilder(Material.IRON_BOOTS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.DEPTH_STRIDER, 3)
                .build();

        weapon = new ItemBuilder(Material.STONE_SWORD)
                .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                .build();

        ItemStack icon = new ItemStack(Material.STAINED_GLASS);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "I am the sorcerer supreme!",
                "",
                ChatColor.GRAY + "A chance to gain absorption when you are under",
                ChatColor.GRAY + "3 hearts for 5 seconds!"
        );
    }

    @EventHandler
    public void damageEvent(EntityDamageEvent event){
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Kit kit = KitPvP.getInstance().getStats(player).getActiveKit().getKit();
            if (kit.getKitType() == KitType.SORCERER) {
                if (!addCooldown(player, "Absorption", abiltiyCooldown, false))
                    return;

                double health = player.getHealth();

                if (health < 6) {
                    Random random = new Random();
                    int chance = random.nextInt(100);
                    if (chance <= absorptionChance) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 5, 1)); // 4 hearts of absorption
                    }
                }
            }
        }
    }

}
