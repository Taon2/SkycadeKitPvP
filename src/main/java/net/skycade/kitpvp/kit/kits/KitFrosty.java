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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitFrosty extends Kit {

	private final List<Player> snowballCooldown = new ArrayList<>();

	public KitFrosty(KitManager kitManager) {
		super(kitManager, "Frosty", KitType.FROSTY, 15000, "Always ready for a snowball fight");
		setIcon(new ItemStack(Material.SNOW_BALL));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 0 : 1).build());
		p.getInventory().addItem(new ItemBuilder(Material.SNOW_BALL, level == 1 ? 6 : 8).build());
		
		p.getInventory().setHelmet(new ItemBuilder(Material.JACK_O_LANTERN).addEnchantment(Enchantment.DURABILITY, 2).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level == 1 ? 3 : 4).build());
		p.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build());
		p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level == 3 ? 4 : 3).build());
		startItemRunnable(p, 20, new ItemBuilder(Material.SNOW_BALL).build(), 8, KitType.FROSTY);
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
