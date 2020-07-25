package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.CURRENT_COMBO;

public class KitStrafe extends Kit {

    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private final Map<UUID, Integer> comboMap = new HashMap<>();

    public KitStrafe(KitManager kitManager) {
        super(kitManager, "Strafe", KitType.STRAFE, 41000, false, getLore());

        boots = new ItemBuilder(
                Material.DIAMOND_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
        weapon = new ItemBuilder(
                Material.STONE_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Comboing players deals extra damage.").build();

        constantEffects.put(PotionEffectType.SPEED, 3);
        constantEffects.put(PotionEffectType.INCREASE_DAMAGE, 0);

        ItemStack icon = new ItemStack(Material.DIAMOND_BOOTS);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
        if (!comboMap.containsKey(damager.getUniqueId())) {
            comboMap.put(damager.getUniqueId(), 1);
            return;
        }
        int combo = comboMap.get(damager.getUniqueId()) + 1;
        damager.setLevel(combo);
        if (combo > 0 && (combo % 3 == 0))
            CURRENT_COMBO.msg(damager, "%combo%", Integer.toString(combo));

        double dmgInc = 1.0;
        while (combo >= 3) {
            dmgInc += 0.1;
            combo -= 3;
        }
        event.setDamage(event.getFinalDamage() * (Math.min(dmgInc, 1.5)));
    }

    @Override
    public void onDamageGetHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
        comboMap.remove(damagee.getUniqueId());
    }

    @Override
    public void onMove(Player p) {
        particleTracerEffect(p, Color.RED, 30);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        comboMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Nyoom!",
                "",
                ChatColor.GRAY + "Comboing someone makes",
                ChatColor.GRAY + "your hits more deadly."
        );
    }
}
