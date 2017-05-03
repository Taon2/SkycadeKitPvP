package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class KitPrick extends Kit {

	public KitPrick(KitManager kitManager) {
		super(kitManager, "Prick", KitType.PRICK, 22000, "A little spiky");
		setIcon(Material.CACTUS);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.KNOCKBACK, level == 3 ? 0 : 1).addEnchantment(Enchantment.DAMAGE_ALL, level == 1 ? 0 : 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, level * 2 + 12, 2, Color.GREEN));
		p.getInventory().addItem(new ItemBuilder(Material.CACTUS).build());
		for (ItemStack piece : p.getInventory().getArmorContents())
			piece.addEnchantment(Enchantment.THORNS, level);
	}
	
	@Override
	public void onInteract(Player p, Player target, ItemStack item) {
		if (item.getType() != Material.CACTUS)
			return;
		int level = getLevel(p);
		if (!addCooldown(p, getName(), 20 - (3 * level), true))
			return;
		
		ItemStack[] armor = target.getEquipment().getArmorContents();
		target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, level == 3 ? 130 : 50 * level, 1));

		for (ItemStack anArmor : armor)
			if (anArmor != null)
				anArmor.setDurability((short) (anArmor.getDurability() + 20));
		target.getInventory().setArmorContents(armor);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use the cactus to poison someone", "§7this has a big effect on the armor", "§7durability of your target");
	}

}
