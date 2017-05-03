package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class KitMaster extends Kit {

	public KitMaster(KitManager kitManager) {
		super(kitManager, "KitMaster", KitType.KITMASTER, 0, "You are the true master of KitPvP");
		// This kit unlocks once all other kits are unlocked.
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.DIAMOND_SPADE).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, 5).build());
		p.getInventory().addItem(new ItemBuilder(Material.STICK).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 5, 5, Color.fromBGR(0, 215, 255)));
	}

	@Override
	public void onInteract(Player p, Player target, ItemStack item) {
		if (item.getType() != Material.STICK)
			return;
		if (!addCooldown(p, getName(), 40, true))
			return;
		Kit targetKit = getKitManager().getKitPvP().getStats(target).getActiveKit().getKit();
		if (targetKit == null || targetKit.getKitType() == KitType.KITMASTER)
			return;

		HashMap<Integer, ItemStack> invItems = new HashMap<>();
		PlayerInventory inv = p.getInventory();
		for (Integer i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) != null)
				invItems.put(i, inv.getItem(i));

		ItemStack[] armor = p.getInventory().getArmorContents();
		final int soupAmount = getSoupAmount(inv);
		clearInventory(p);

		targetKit.applyKit(p, 3);
		getKitManager().getKitPvP().getStats(p).setActiveKit(targetKit.getKitType());
		targetKit.giveSoup(p, soupAmount);
		p.sendMessage("§bYou copied the " + targetKit.getName() + " kit, you will return to the KitMaster kit in 20 seconds");
		target.sendMessage("§b" + p.getName() + "§f copied your kit!");
		kitMasterRunnable(p, armor, invItems);
	}

	private void clearInventory(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
	}

	private void kitMasterRunnable(Player p, ItemStack[] playerArmor, HashMap<Integer, ItemStack> invItems) {
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> {
			getKitManager().getKits().get(KitType.KITMASTER).applyKit(p);
			getKitManager().getKitPvP().getStats(p).setActiveKit(KitType.KITMASTER);
			clearInventory(p);

			p.getInventory().setArmorContents(playerArmor);
			for (Entry<Integer, ItemStack> entry : invItems.entrySet())
				p.getInventory().setItem(entry.getKey(), entry.getValue());
			for (PotionEffect effect : p.getActivePotionEffects())
				p.removePotionEffect(effect.getType());
		} , 20 * 20);
	}
	
	private int getSoupAmount(Inventory inv) {
		int amount = 0;
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) != null)
				if (inv.getItem(i).getType() == Material.MUSHROOM_SOUP)
					amount++;
		return amount;
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use the stick to copy a kit", "from someone");
	}

}
