package net.skycade.kitpvp.commands.staff;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.coreclasses.utils.UtilString;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CommandTeam extends Command<KitManager> implements Listener {

	final Map<String, List<Player>> teamMap = new HashMap<>();
	final Map<String, String> playerTeam = new HashMap<>();

	public CommandTeam(KitManager module) {
		super(module, "Manage teams", new Permission("kitpvp.admin", PermissionDefault.OP), "team");
		setUsage("<create/remove/teleport/setkit>");
		Bukkit.getPluginManager().registerEvents(this, getModule().getPlugin());
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args, 2))
			return;
		final String teamName = UtilString.capitaliseFirstCharacter(args[1].toLowerCase());

		if (args[0].equalsIgnoreCase("create")) {
			if (args.length <= 2) {
				member.message("§7/§cteam §7<§ccreate> <teamName> <player1> <player2§7>");
				return;
			}
			if (teamMap.containsKey(teamName)) {
				member.message(teamName + " already exists.");
				return;
			}

			List<Player> players = new ArrayList<>();
			for (int i = 2 ; i < args.length ; i++) {
				if (!getPlayer(member, args[i]))
					return;
				else if (playerTeam.containsKey(Bukkit.getPlayer(args[i]).getName())) {
					member.message("§7player is already in §a" + playerTeam.get(Bukkit.getPlayer(args[i]).getName() + "§7."));
					return;
				}
				players.add(Bukkit.getPlayer(args[i]));
			}

			players.forEach(target -> {
				target.sendMessage("§7You are added to the §a" + teamName + " §7team.");
				playerTeam.put(target.getName(), teamName);
			});
			teamMap.put(teamName, players);
			member.message("§a" + teamName + "§7 got created.");
			return;
		}
		if (!teamMap.containsKey(teamName)) {
			couldNotFind(member, "teamName", args[1]);
			return;
		}
		if (args[0].equalsIgnoreCase("teleport")) {
			teamMap.get(teamName).forEach(target -> target.teleport(member.getPlayer().getLocation()));
			member.message("§7" + teamName + " members §7got §ateleported §7to your §alocation§7.");
			return;
		} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("destroy")) {
			teamMap.get(teamName).forEach(target -> target.sendMessage("§7Your team got §adeleted§7."));
			teamMap.remove(teamName);
			member.message("§a" + teamName + "§7 got deleted.");
			return;
		} else if (args[0].equalsIgnoreCase("setkit")) {
			if (args.length <= 2) {
				member.message("§7/§cteam §7<§c<setkit> <teamName> <kit> <level>§7>");
				return;
			}
			if (!checkArgs(member, aliasUsed, args, 3))
				return;
			Kit kit = null;
			for (Entry<KitType, Kit> entry : getModule().getKits().entrySet())
				if (entry.getValue().getName().equalsIgnoreCase(args[2]))
					kit = entry.getValue();
			if (kit == null) {
				couldNotFind(member, "kitname", args[2]);
				return;
			}
			int kitLevel = 3;
			if (args.length == 4 && parseInt(member, args[3]))
				kitLevel = Integer.parseInt(args[3]);
			for (Player target : teamMap.get(teamName)) {
				UtilPlayer.reset(target);
				kit.applyKit(target, kitLevel);
				kit.giveSoup(target, 32);
				KitType oldKit = getModule().getKitPvP().getStats(target).getActiveKit();
				getModule().getKitPvP().getStats(target).setActiveKit(kit.getKitType());
				getModule().getKitPvP().getStats(target).setKitPreference(oldKit);
			}
			member.message("§a" + teamName + "§7 got the §a" + kit.getName() + "§7 kit with level §a" + kitLevel + ".");
			return;
		}
		member.message(getUsageToString());
	}


	@EventHandler
	public void onTeamDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player))
			return;
		Player damager = (Player) e.getDamager();
		Player damagee = (Player) e.getEntity();
		if (playerTeam.containsKey(damager.getName()) && playerTeam.containsKey(damagee.getName()))
			if (playerTeam.get(damager.getName()) == playerTeam.get(damagee.getName()))
				e.setCancelled(true);
	}
}