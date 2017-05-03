package me.bukkit.kitpvp.kit.kits;

import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.UtilPlayer;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitManager;
import me.bukkit.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitCaveMan extends Kit {

	private int blockCounter = 0;
	private final List<UUID> abilityCooldown = new ArrayList<>();

	public KitCaveMan(KitManager kitManager) {
		super(kitManager, "Caveman", KitType.CAVEMAN, 7000, "Crazy caveman guy!");
		setIcon(Material.WOOD_SPADE);
	}

	@Override
	public void applyKit(Player p, int level) {
		p.getInventory().addItem(new ItemBuilder(Material.WOOD_SPADE).addEnchantment(Enchantment.DURABILITY, 7).addEnchantment(Enchantment.DAMAGE_ALL, level == 3 ? 6 : 5).build());
        Collections.singletonList(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, level == 1 ? 0 : 1));
		p.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).build());
		p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, 12).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level == 1 ? 2 : 3).build());
		p.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level).addEnchantment(Enchantment.DURABILITY, level == 3 ? 2 : 0).build());
	}

	@SuppressWarnings("deprecation")	
	@Override
	public void onItemUse(Player p, ItemStack item) {
		if (item.getType() != Material.WOOD_SPADE)
			return;
		int level = getLevel(p);
		if (abilityCooldown.contains(p.getUniqueId()))
			return;
		abilityCooldown.add(p.getUniqueId());
		Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> abilityCooldown.remove(p.getUniqueId()), (level == 3 ? 2 : 3) * 20);

		Block currentBlock = p.getLocation().subtract(0, 1, 0).getBlock();
		if (currentBlock.getType() == Material.AIR)
			currentBlock = p.getLocation().subtract(0, 2, 0).getBlock();
		if (currentBlock.getType() == Material.AIR)
			return;

		final FallingBlock b = p.getWorld().spawnFallingBlock(p.getEyeLocation(), Material.DIRT, currentBlock.getData());
		b.setVelocity(p.getLocation().getDirection().multiply(1D));
		
		new BukkitRunnable() {
			public void run() {
				Block block = b.getLocation().subtract(0, 1, 0).getBlock();
				if (b.isOnGround() || block.getLocation().distance(p.getLocation()) < 0.1 || blockCounter > 20 || b.isOnGround()) {
					if (b.getLocation().getBlock().getType() == Material.DIRT)
						b.getLocation().getBlock().setType(Material.AIR);
					b.remove();
					blockCounter = 0;
					this.cancel();
					return;
				}
				blockCounter++;
				UtilPlayer.getNearbyPlayers(b.getLocation(), 2).forEach(player -> {
					if (player != p) 
						if (Arrays.asList(GameMode.SURVIVAL, GameMode.ADVENTURE).contains(player.getGameMode())) 
							player.damage(8 + (level * 3), p);
				});
			}
		}.runTaskTimer(getKitManager().getPlugin(), 0, 2);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		abilityCooldown.remove(e.getPlayer().getUniqueId());
	}
	
	@Override
	public List<String> getAbilityDesc() {
		return Arrays.asList("§7Use your shovel to throw a dirt block", "§7higher level results in a lower cooldown");
	}
	
}
