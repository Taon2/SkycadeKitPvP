package net.skycade.kitpvp.runnable;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class CrateRunnable extends BukkitRunnable {

    private final Member member;
    private int counter;
	private int randomNumber;
	private Sound sound;
	private Player p;
	private Inventory inv;
	private List<KitType> crateItems;
	private KitPvPStats stats;

	public CrateRunnable(int randomNumber, Player p, Member member, List<KitType> crateItems, KitPvPStats stats) {
		this.randomNumber = randomNumber;
		this.p = p;
		this.crateItems = crateItems;		
		this.stats = stats;
		this.member = member;
		counter = -1;
		inv = Bukkit.createInventory(p, 54, "§aCrate");
		sound = Arrays.asList(Sound.NOTE_PLING, Sound.NOTE_SNARE_DRUM, Sound.NOTE_BASS, Sound.NOTE_STICKS, Sound.NOTE_PIANO).get(UtilMath.getRandom(0, 4));
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
			MemberManager.getInstance().update(member);
			p.sendMessage("§7You won the §a" + prize.getName() + "§7 kit.");
			donatorCratePerk(MemberManager.getInstance().getMember(p), prize);

			if (KitPvP.getInstance().getAvailableKits() - 1 == stats.getKits().size()) {
				p.sendMessage("§7You unlocked §aall §7the kits! You unlocked the §aKitMaster §7kit.");
				stats.addKit(KitType.KITMASTER);
			}
		}
	}

	private void donatorCratePerk(Member member, Kit prize) {
		int random = UtilMath.getRandom(0, 100);
		/* if (member.hasPermission(Permission.RANK_FIVE)) { */
			if (checkUnlock(member, random, 40, 2, prize))
				return;
			if (checkUnlock(member, random, 50, 3, prize))
                return;
		/* } else if (member.hasPermission(Permission.RANK_THREE)) {
			if (checkUnlock(member, random, 30, 2, prize))
				return;
		} else if (member.hasPermission(Permission.RANK_ONE)) {
			if (checkUnlock(member, random, 15, 2, prize))
				return;
		} */
	}
	
	private boolean checkUnlock(Member member, int random, int required, int level, Kit prize) {
	    return false; // no more levels after 1
		/* if (random <= required) {
			member.message("You're lucky, " + prize.getName() + " upgraded to level " + level + " because of your donator perk.");
			stats.getKits().get(prize.getKitType()).setLevel(level);
			return true;
		}
		return false; */
	}

}
