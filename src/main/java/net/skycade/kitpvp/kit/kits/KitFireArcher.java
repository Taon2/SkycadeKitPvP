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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class KitFireArcher extends Kit {

	private final List<UUID> bowCooldown = new ArrayList<>();
	private final List<UUID> flameCooldown = new ArrayList<>();

	public KitFireArcher(KitManager kitManager) {
		super(kitManager, "FireArcher", KitType.FIREARCHER, 24000, "Some of his arrows are", "Enchanted with fire");
		setIcon(new ItemStack(Material.ARROW));
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level).build());
		p.getInventory().addItem(new ItemBuilder(Material.BOW).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.ARROW_INFINITE, 1).addEnchantment(Enchantment.ARROW_DAMAGE, level == 1 ? 1 : 2).build());
		p.getInventory().addItem(new ItemBuilder(Material.ARROW, 1).build());
		p.getInventory().setArmorContents(getArmour(Material.LEATHER_HELMET, 5, level));
		p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).addEnchantment(Enchantment.DURABILITY, 10).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level <= 2 ? 2 : 3).setColour(Color.ORANGE).build()); 
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.BOW)
			return;
		if (flameCooldown.contains(p.getUniqueId()))
			return;
		int level = getLevel(p);
		int flameSeconds = 5 + (level * 5);
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


