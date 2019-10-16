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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

import static java.lang.Integer.parseInt;
import static net.skycade.kitpvp.Messages.GET_SPOOKED;

public class KitGhost extends Kit {

    public KitGhost(KitManager kitManager) {
        super(kitManager, "Ghost", KitType.GHOST, 22000, false, "Very spooky");
        setIcon(new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 8270)).build());

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "POTION");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 22000);

        defaultsMap.put("inventory.sword.material", "DIAMOND_SWORD");
        defaultsMap.put("inventory.sword.enchantments.durability", 5);
        defaultsMap.put("inventory.sword.enchantments.knockback", 1);
        defaultsMap.put("inventory.sword.enchantments.damage-all", 2);

        defaultsMap.put("potions.pot1", "INVISIBILITY:10");
        defaultsMap.put("potions.pot2", "DAMAGE_RESISTANCE:1");

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
                Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
                .addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
                .addEnchantment(Enchantment.KNOCKBACK, getConfig().getInt("inventory.sword.enchantments.knockback"))
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1]), false, false));

        String[] pot2 = getConfig().getString("potions.pot2").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot2[0]),
                Integer.MAX_VALUE,
                parseInt(pot2[1])));
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.DIAMOND_SWORD)
            return;
        if (!addCooldown(p, getName(), 10, true))
            return;

        int range = 4;
        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), range);
        if (targetPlayers.size() <= 1)
            removeCooldowns(p, getName());

        targetPlayers.forEach(target -> {
            if (!target.equals(p)) {
                GET_SPOOKED.msg(p);
                levitateInAir(target, 40);
            }
        });
    }

    private void levitateInAir(Player target, Integer ticks) {
        if (ticks > 0) {
            int remainingTicks = ticks - 1;
            Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
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
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Right click with your sword", "§7to spook players around you");
    }

}
