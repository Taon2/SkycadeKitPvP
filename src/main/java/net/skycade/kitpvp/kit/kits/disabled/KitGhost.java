package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

import static net.skycade.kitpvp.Messages.GET_SPOOKED;

public class KitGhost extends Kit {

    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    private int spookCooldown = 10;

    public KitGhost(KitManager kitManager) {
        super(kitManager, "Ghost", KitType.GHOST, 22000, false, getLore());

        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                .addEnchantment(Enchantment.KNOCKBACK, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + spookCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "levitates players around you.").build();

        constantEffects.put(PotionEffectType.INVISIBILITY, 10);
        constantEffects.put(PotionEffectType.DAMAGE_RESISTANCE, 1);

        ItemStack icon = new ItemStack(Material.POTION);
        icon.setDurability((short) 8270);
        setIcon(icon);

        setIcon(new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 8270)).build());
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.DIAMOND_SWORD)
            return;
        if (!addCooldown(p, "Spook", spookCooldown, true))
            return;

        int range = 4;
        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p, p.getLocation(), range);
        if (targetPlayers.size() <= 1)
            removeCooldowns(p, getName());

        targetPlayers.forEach(target -> {
            GET_SPOOKED.msg(p);
            levitateInAir(target, 40);
        });
    }

    private void levitateInAir(Player target, Integer ticks) {
        if (ticks > 0) {
            int remainingTicks = ticks - 1;
            Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
                particleEffect(target);
                target.setVelocity(new Vector(0, 0.1, 0));
                levitateInAir(target, remainingTicks);
            }, 1);
        }
    }

    private void particleEffect(Player target) {
        for (int i = 0; i < 5; i++)
            ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(Color.WHITE), target.getLocation().add(0, 0.1F, 0), 1F);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "OooOOOooo!",
                "",
                ChatColor.GRAY + "Right clicking will",
                ChatColor.GRAY + "spook players around you."
        );
    }
}
