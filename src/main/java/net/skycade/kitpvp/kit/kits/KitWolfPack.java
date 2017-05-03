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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitWolfPack extends Kit {

	public KitWolfPack(KitManager kitManager) {
		super(kitManager, "Wolfpack", KitType.WOLFPACK, 25000, "Tamer of beasts");
		setIcon(Material.BONE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 0 : level - 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, 4, Color.WHITE));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(),  level == 3 ? 20 : 30, false))
			return;
		List<Wolf> wolfList = new ArrayList<>();
	
		int wolfAmount = level == 3 ? 2 : level;
		for (int i = 0 ; i < wolfAmount ; i++) {
			Wolf wolf = (Wolf) p.getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
			wolf.setCustomName(p.getName() + "'s" + " wolf");
			wolf.setOwner(p);
			wolf.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2));
			wolfList.add(wolf);
		}
		removeWolf(10 + (level * 3), wolfList);
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
