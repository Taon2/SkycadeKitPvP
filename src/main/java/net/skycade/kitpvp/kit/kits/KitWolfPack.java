package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitWolfPack extends Kit {

	public KitWolfPack(KitManager kitManager) {
		super(kitManager, "Wolfpack", KitType.WOLFPACK, 25000, "Tamer of beasts");
		setIcon(Material.BONE);

		Map<String, Object> defaultsMap = new HashMap<>();

		defaultsMap.put("inventory.sword.material", "IRON_SWORD");
		defaultsMap.put("inventory.sword.enchantments.durability", 5);
		defaultsMap.put("inventory.sword.enchantments.damage-all", 0);

		defaultsMap.put("armor.material", "LEATHER");
		defaultsMap.put("armor.enchantments.durability", 12);
		defaultsMap.put("armor.enchantments.protection", 4);

		setConfigDefaults(defaultsMap);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(
				Material.getMaterial(getConfig().getString("inventory.sword.material").toUpperCase()))
				.addEnchantment(Enchantment.DURABILITY, getConfig().getInt("inventory.sword.enchantments.durability"))
				.addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.sword.enchantments.damage-all")).build());

		p.getInventory().setArmorContents(getArmour(
				Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
				getConfig().getInt("armor.enchantments.durability"),
				getConfig().getInt("armor.enchantments.protection"),
				Color.WHITE));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 20, false))
			return;
		List<Wolf> wolfList = new ArrayList<>();
	
		int wolfAmount = 2;
		for (int i = 0 ; i < wolfAmount ; i++) {
			Wolf wolf = (Wolf) p.getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
			wolf.setCustomName(p.getName() + "'s" + " wolf");
			wolf.setOwner(p);
			wolf.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
			wolfList.add(wolf);
		}
		removeWolf(19, wolfList);
	}
	
	private void removeWolf(Integer seconds, List<Wolf> wolfList) {
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
			for (Wolf wolf : wolfList)
				wolf.remove();
		}, seconds * 20);
	}

	@Override
	public List<String> getAbilityDesc() {
		return Collections.singletonList("§7Use your sword ability to spawn wolfs");
	}
	
	@EventHandler
	public void onPlayerHit(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Wolf && e.getEntity() instanceof Player))
			return;
		e.setDamage(e.getDamage() * 2.5);
	}

}
