package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class KitMedic extends Kit {

	public KitMedic(KitManager kitManager) {
		super(kitManager, "Medic", KitType.MEDIC, 16000, "Helpful in battle");
		setIcon(Material.SHEARS);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_HOE).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 5 : 6).build());
		p.getInventory().addItem(new ItemBuilder(Material.LEATHER, 5).addLore(Arrays.asList("§cDrop to heal", "§cPlayers around you")).setName("§cMedpack").build());
		p.getInventory().addItem(new ItemBuilder(Material.SHEARS).addLore("§cCan be used to heal a player.").build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 12, level == 1 ? 3 : (level - 1) + 4, Color.RED));
		startItemRunnable(p, 20 - (level * 4), new ItemBuilder(Material.LEATHER).addLore(Arrays.asList("§cDrop to heal", "§cPlayers around you")).setName("§cMedpack").build(), 8, KitType.MEDIC);
	}
	
	public void onMedpackUse(Player p, Item medpack) {
		int level = getLevel(p);
		
		Set<Player> players = UtilPlayer.getNearbyPlayers(p.getLocation(), 3);
		players.remove(p);

		players.forEach((player) -> {
			player.setHealth(player.getMaxHealth());
			player.sendMessage("§cHealed");		
			if (level > 1)
				 player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
		});
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), medpack::remove, 8);
	}
	
	@Override
	public void onInteract(Player p, Player target, ItemStack item) {
		if (item.getType() != Material.SHEARS)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, "Shears", 10 - (level * 2), false))
			return;
		target.setHealth(target.getMaxHealth());
		p.sendMessage("§c" + target.getName() + " got healed.");
		target.sendMessage("§cHealed!");
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Throw down medpacks to heal", "§7players around you and", "§7use the shears to heal someone");
	}

}
