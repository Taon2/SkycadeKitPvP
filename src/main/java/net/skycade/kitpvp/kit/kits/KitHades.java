package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitHades extends Kit {

	public KitHades(KitManager kitManager) {
		super(kitManager, "Hades", KitType.HADES, 45000, "Hades has a burning aura around him");
		setIcon(Material.NETHERRACK);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);
		defaultsMap.put("inventory.sword.enchantments.fire-aspect", 0);

		defaultsMap.put("armor.helmet.material", "LEATHER");
		defaultsMap.put("armor.helmet.enchantments.durability", 12);
		defaultsMap.put("armor.helmet.enchantments.protection", 4);

		defaultsMap.put("armor.chestplate.material", "LEATHER");
		defaultsMap.put("armor.chestplate.enchantments.durability", 12);
		defaultsMap.put("armor.chestplate.enchantments.protection", 3);

		defaultsMap.put("armor.leggings.material", "LEATHER");
		defaultsMap.put("armor.leggings.enchantments.durability", 12);
		defaultsMap.put("armor.leggings.enchantments.protection", 4);

		defaultsMap.put("armor.boots.material", "LEATHER");
		defaultsMap.put("armor.boots.enchantments.durability", 12);
		defaultsMap.put("armor.boots.enchantments.protection", 4);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all"))
				.addEnchantment(Enchantment.FIRE_ASPECT, getConfig().getInt("inventory.sword.enchantments.fire-aspect")).build());
		
		p.getInventory().setHelmet(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
				.setColour(Color.fromBGR(0, 0, 102)).build());

		p.getInventory().setChestplate(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.chestplate.enchantments.protection"))
				.setColour(Color.fromBGR(0, 0, 150)).build());

		p.getInventory().setLeggings(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection"))
				.setColour( Color.fromBGR(0, 0, 200)).build());

		p.getInventory().setBoots(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection"))
				.setColour(Color.fromBGR(51, 51, 255)).build());
	}
	
	@Override
	public void onMove(Player p) {
		particleMoveEffect(p, ParticleEffect.FLAME, 1, 30);
		if (UtilPlayer.isMoving(p) && !getKitManager().getKitPvP().getSpawnRegion().contains(p)) {

            UtilPlayer.getNearbyPlayers(p.getLocation(), 2).forEach(target -> {
                if (target != p)
                    target.setFireTicks(3 * 20);
            });

        }
	}

	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Players around you will be set", "§7on fire if you're moving");
	}

}
