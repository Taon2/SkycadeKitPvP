package me.bukkit.kitpvp.runnable;

import me.bukkit.kitpvp.coreclasses.member.Member;
import me.bukkit.kitpvp.coreclasses.member.MemberManager;
import me.bukkit.kitpvp.coreclasses.member.Permission;
import me.bukkit.kitpvp.kit.Kit;
import me.bukkit.kitpvp.kit.KitType;
import me.bukkit.kitpvp.stat.KitPvPStats;
import me.bukkit.kitpvp.coreclasses.utils.ItemBuilder;
import me.bukkit.kitpvp.coreclasses.utils.UtilMath;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class CrateRunnable extends BukkitRunnable {

	private int counter;
	private int randomNumber;
	private Sound sound;
	private Player p;
	private Inventory inv;
	private List<KitType> crateItems;
	private KitPvPStats stats;

	public CrateRunnable(int randomNumber, Player p, List<KitType> crateItems, KitPvPStats stats) {
		this.randomNumber = randomNumber;
		this.p = p;
		this.crateItems = crateItems;		
		this.stats = stats;
		counter = -1;
		inv = Bukkit.createInventory(p, 54, "§aCrate");
		sound = Arrays.asList(Sound.NOTE_PLING, Sound.NOTE_PIANO, Sound.NOTE_SNARE_DRUM, Sound.NOTE_STICKS, Sound.NOTE_BASS).get(UtilMath.getRandom(0, 4));
	}

	@Override
	public void run() {
		if (counter < randomNumber) {
			if (counter == -1)
				p.openInventory(inv);
			else {
				inv.clear();
				Kit kit = crateItems.get(counter).getKit();
				inv.setItem(counter, new ItemBuilder(kit.getIcon()).setName(kit.getName()).build());
				p.getWorld().playSound(p.getLocation(), sound, 1, 1);
				p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
			}
			counter++;
		} else {
			this.cancel();
			p.closeInventory();
			Kit prize = crateItems.get(counter <= 0 ? 0 : counter - 1).getKit();
			stats.addKit(prize.getKitType());
			p.sendMessage("§7You won the §a" + prize.getName() + "§7 kit.");
			donatorCratePerk(MemberManager.getInstance().getMember(p), prize);

			if (KitType.values().length - 1 == stats.getKits().size()) {
				p.sendMessage("§7You unlocked §aall §7the kits! You unlocked the §aKitMaster §7kit.");
				stats.addKit(KitType.KITMASTER);
			}
		}
	}

	private void donatorCratePerk(Member member, Kit prize) {
		int random = UtilMath.getRandom(0, 100);
		if (member.hasPermission(Permission.RANK_FIVE)) {
			if (checkUnlock(member, random, 40, 2, prize))
				return;
			if (checkUnlock(member, random, 50, 3, prize))
                return;
		} else if (member.hasPermission(Permission.RANK_THREE)) {
			if (checkUnlock(member, random, 30, 2, prize))
				return;
		} else if (member.hasPermission(Permission.RANK_ONE)) {
			if (checkUnlock(member, random, 15, 2, prize))
				return;
		}
	}
	
	private boolean checkUnlock(Member member, int random, int required, int level, Kit prize) {
		if (random <= required) {
			member.message("You're lucky, " + prize.getName() + " upgraded to level " + level + " because of your donator perk.");
			stats.getKits().get(prize.getKitType()).setLevel(level);
			return true;
		}
		return false; 
	}

}
