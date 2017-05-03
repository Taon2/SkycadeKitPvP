package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KitPotionMaster extends Kit {

	public KitPotionMaster(KitManager kitManager) {
		super(kitManager, "PotionMaster", KitType.POTIONMASTER, 20000, "Use splash potions instead of soup");
		setIcon(Material.BREWING_STAND_ITEM);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level - 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 6, 10, Color.fromBGR(UtilMath.getRandom(0, 255), UtilMath.getRandom(0, 255), UtilMath.getRandom(0, 255))));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.IRON_SWORD)
			return;
		int level = getLevel(p);
		if (level == 1)
			return;
		if (!addCooldown(p, getName(), level == 2 ? 45 : 30, true))
			return;
		
		for (int i = 0; i < 4; i++)
			p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 1);
		
		Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 4);
		if (targetPlayers.contains(p))
			targetPlayers.remove(p);

		if (targetPlayers.isEmpty()) {
			removeCooldowns(p);
			return;
		}
		
		targetPlayers.forEach(target -> {
			target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, level == 2 ? 120 : 180, 2));
			target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 120, 1));
		});
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword to throw potions on", "§7the ground, you will get", "§7this ability from level 2");
	}

}
