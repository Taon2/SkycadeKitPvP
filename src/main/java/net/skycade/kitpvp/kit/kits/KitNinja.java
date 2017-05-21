package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class KitNinja extends Kit {
	
	private final List<UUID> ninjaCooldown = new ArrayList<>();

	public KitNinja(KitManager kitManager) {
		super(kitManager, "Ninja", KitType.NINJA, 32000, false, "Dash to deal the damage.");
		setIcon(new ItemBuilder(Material.LEATHER_BOOTS).setColour(Color.BLACK).build());
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.STONE_SWORD).addEnchantment(Enchantment.DURABILITY, 5).addEnchantment(Enchantment.DAMAGE_ALL, level + 2).build());
		p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColour(Color.BLACK).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level + 3).build());
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
	}
	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.STONE_SWORD)
			return;
		if (ninjaCooldown.contains(p.getUniqueId()))
			return; 
		ninjaCooldown.add(p.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> ninjaCooldown.remove(p.getUniqueId()), 60);
		p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0));
		tpDash(p, 6);
	}
	
	private void tpDash(Player p, int range) {
		final Location playerLoc = p.getLocation();
        double nX;
        double nZ;
        float nang = playerLoc.getYaw() + 90;
        if(nang < 0) 
        	nang += 360;
        nX = Math.cos(Math.toRadians(nang)) * range;
        nZ = Math.sin(Math.toRadians(nang)) * range;

        Location newLoc = new Location(playerLoc.getWorld(), playerLoc.getX() + nX, playerLoc.getY(), playerLoc.getZ() + nZ, playerLoc.getYaw(), playerLoc.getPitch());
        if (!isValidBlock(newLoc.getBlock().getType()) || (newLoc.getBlock().getType() != Material.AIR && newLoc.add(0, 1, 0).getBlock().getType() != Material.AIR && newLoc.add(0, 2, 0).getBlock().getType() != Material.AIR)) {
    		 if (range <= 2) {
    			 return;
    		 } else 
    			 tpDash(p, range - 1);
        }
        p.teleport(newLoc);
        p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);
	}

	@Override
	public void onMove(Player p) {
		particleTracerEffect(p, Color.PURPLE, 30);
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your sword to dash around", "§7dashing will give you a short strength buff");
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e) {
		ninjaCooldown.remove(e.getPlayer().getUniqueId());
	}

}
