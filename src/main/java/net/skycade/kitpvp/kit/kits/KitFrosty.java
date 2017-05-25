package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitFrosty extends Kit {

	private final List<Player> snowballCooldown = new ArrayList<>();

	public KitFrosty(KitManager kitManager) {
		super(kitManager, "Frosty", KitType.FROSTY, 15000, "Always ready for a snowball fight");
		setIcon(new ItemStack(Material.SNOW_BALL));

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("inventory.snowball.amount", 6);
		defaultsMap.put("inventory.snowball.max-amount", 8);

		defaultsMap.put("armor.helmet.enchantments.durability", 2);

		defaultsMap.put("armor.chestplate.material", "LEATHER");
		defaultsMap.put("armor.chestplate.enchantments.durability", 12);
		defaultsMap.put("armor.chestplate.enchantments.protection", 3);

		defaultsMap.put("armor.leggings.material", "LEATHER");
		defaultsMap.put("armor.leggings.enchantments.durability", 12);
		defaultsMap.put("armor.leggings.enchantments.protection", 3);

		defaultsMap.put("armor.boots.material", "LEATHER");
		defaultsMap.put("armor.boots.enchantments.durability", 12);
		defaultsMap.put("armor.boots.enchantments.protection", 3);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().addItem(new ItemBuilder(
				Material.SNOW_BALL, getConfig().getInt("inventory.snowball.amount")).build());
		
		p.getInventory().setHelmet(new ItemBuilder(
				Material.JACK_O_LANTERN)
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability")).build());

		p.getInventory().setChestplate(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.chestplate.material").toUpperCase() + "_CHESTPLATE"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.chestplate.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.chestplate.enchantments.protection")).build());

		p.getInventory().setLeggings(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.leggings.material").toUpperCase() + "_LEGGINGS"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.leggings.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.leggings.enchantments.protection")).build());

		p.getInventory().setBoots(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.boots.material").toUpperCase() + "_BOOTS"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.boots.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.boots.enchantments.protection")).build());

		startItemRunnable(p, 20, new ItemBuilder(Material.SNOW_BALL).build(), getConfig().getInt("inventory.snowball.max-amount"), KitType.FROSTY);
	}

	public void onSnowballUse(Player shooter, ProjectileLaunchEvent e) {
		if (snowballCooldown.contains(shooter)) {
			shooter.getItemInHand().setAmount(shooter.getItemInHand().getAmount() + 1);
			e.setCancelled(true);
			return;
		}
		snowballCooldown.add(shooter);
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> snowballCooldown.remove(shooter), 10);
		e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(2));
	}

	public void onSnowballHit(Player shooter, Player damagee) {
		damagee.sendMessage("§bYou got frozen.");
		freezePlayer(damagee, 5);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your snowballs to freeze players", "§7you will regain them overtime");
	}
	
}
