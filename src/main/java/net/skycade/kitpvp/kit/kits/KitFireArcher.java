package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitFireArcher extends Kit {

	private final List<UUID> bowCooldown = new ArrayList<>();
	private final List<UUID> flameCooldown = new ArrayList<>();

	public KitFireArcher(KitManager kitManager) {
		super(kitManager, "FireArcher", KitType.FIREARCHER, 24000, "Some of his arrows are", "Enchanted with fire");
		setIcon(new ItemStack(Material.ARROW));

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "STONE_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 1);

		defaultsMap.put("inventory.bow.enchantments.durability", 5);
		defaultsMap.put("inventory.bow.enchantments.arrow-infinite", 1);
		defaultsMap.put("inventory.bow.enchantments.arrow-damage", 1);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 5);
		defaultsMap.put("armor.enchantments.protection", 1);

		defaultsMap.put("armor.helmet.material", "LEATHER");
		defaultsMap.put("armor.helmet.enchantments.durability", 10);
		defaultsMap.put("armor.helmet.enchantments.protection", 2);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().addItem(new ItemBuilder(
				Material.BOW)
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.bow.enchantments.durability"))
				.addEnchantment(Enchantment.ARROW_INFINITE, getConfig().getInt("inventory.bow.enchantments.arrow-infinite"))
				.addEnchantment(Enchantment.ARROW_DAMAGE, getConfig().getInt("inventory.bow.enchantments.arrow-damage")).build());

		p.getInventory().addItem(new ItemBuilder(
				Material.ARROW, 1).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection")));

		p.getInventory().setHelmet(new ItemBuilder(
				Material.getMaterial(getConfig().getString("armor.helmet.material").toUpperCase() + "_HELMET"))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("armor.helmet.enchantments.durability"))
				.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, getConfig().getInt("armor.helmet.enchantments.protection"))
				.setColour(Color.ORANGE).build());
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.BOW)
			return;
		if (flameCooldown.contains(p.getUniqueId()))
			return;
		int level = getLevel(p);
		int flameSeconds = 20;
		flameCooldown.add(p.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> flameCooldown.remove(p.getUniqueId()), 60 - flameSeconds);
		
		p.getItemInHand().addEnchantment(Enchantment.ARROW_FIRE, 1);
		p.getWorld().playSound(p.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.0F);

		Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), ()  -> p.getInventory().forEach((itemStack) -> {
            if (itemStack != null && itemStack.getType() == Material.BOW) {
                if (itemStack.containsEnchantment(Enchantment.ARROW_FIRE)) {
                    p.sendMessage(ChatColor.RED + "Fire removed.");
					itemStack.removeEnchantment(Enchantment.ARROW_FIRE);
                }
            }
        }), 20 * flameSeconds);
	}
	

	public void onArrowLaunch(Player shooter, ProjectileLaunchEvent e) {
		if (bowCooldown.contains(shooter.getUniqueId())) {
			e.setCancelled(true);
			return;
		}
		bowCooldown.add(shooter.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> bowCooldown.remove(shooter.getUniqueId()), 18);
	}

	public void onArrowHit(Player shooter, Player damagee, EntityDamageByEntityEvent e) {
		if (UtilMath.getRandom(0, 100) <= getLevel(shooter) * 3)
			damagee.setFireTicks(100);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		bowCooldown.remove(e.getPlayer().getUniqueId());
		flameCooldown.remove(e.getPlayer().getUniqueId());
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7Higher level means a longer fire arrow duration");
	}

}


