package net.skycade.kitpvp.commands;

import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.Permission;
import net.skycade.kitpvp.coreclasses.option.IOption;
import net.skycade.kitpvp.coreclasses.option.None;
import net.skycade.kitpvp.coreclasses.option.Some;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.duel.Duel;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.Achievement;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandDuel extends Command<KitManager> implements Listener {
    
	private final KitManager kitManager;

	public CommandDuel(KitManager module) {
		super(module, "Challenge someone to duel", Permission.NONE, "duel");
		setUsage("<player/accept>", "<kit>");
		this.kitManager = getModule();
		Bukkit.getPluginManager().registerEvents(this, getModule().getPlugin());
	}

	@Override
	public void execute(Member member, String aliasUsed, String... args) {
		if (!checkArgs(member, aliasUsed, args, getUsage().length - 1)) 
		    return;		
		if (args[0].equalsIgnoreCase("accept")) {
			if (!kitManager.getPlayerDuel().containsKey(member.getUUID())) {
				member.message("§7You didn't get invited for a duel.");
				return;
			} 
			if (inDuel(member.getPlayer())) {
			    member.message("You are already in a duel");
			    return;
			}
			Player p1 = member.getPlayer();
			Player p2 = Bukkit.getPlayer(kitManager.getPlayerDuel().get(member.getUUID()));
			
			if (inDuel(p2)) {
			    member.message(p2.getName() + " is already in a duel.");
			    return;
			}
			
			boolean canJoin = false;
			for (Duel duel : kitManager.getDuels()) {
				if (!duel.isRunning()) {
					duel.setRunning(true);
					canJoin = true;
					duel.addPlayers(p1, p2);
					p1.teleport(duel.getLocations()[0]);
					p2.teleport(duel.getLocations()[1]);
					giveKit(p1, kitManager.getDuelKit().get(p2.getUniqueId()));
					giveKit(p2, kitManager.getDuelKit().get(p2.getUniqueId()));
					break;
				}
			}
			if (!canJoin) 
				member.message("§7There are no arena's available right now.");
			return;
		}
		if (!checkArgs(member, aliasUsed, args)) 
            return;
		if (!getPlayer(member, args[0])) {
			couldNotFind(member, "player", args[0]);
			return;
		}
		
		Kit kit = getKit(args).visit(() -> {
            member.message("Kit could not be found and has been set to §adefault§7.");
            return getModule().getKits().get(KitType.DEFAULT);
        }, x -> x); 

		if (!kit.isEnabled()) {
			member.message(kit.getName() + " is disabled.");
			return;
		}
        if (kit.getName().toLowerCase().contains("archer") || kit.getName().equalsIgnoreCase("sniper") || kit.getName().toLowerCase().contains("ninja")) {
            member.message("You're not allowed to use this kit.");
            return;
        }
		Player target = Bukkit.getPlayer(args[0]);
		if (target.getName().equals(member.getPlayer().getName())) {
		    member.message("You can't duel yourself.");
		    return;
		}
		if (inDuel(target)) {
		    member.message(target.getName() + " is currently in a duel.");
		    return;
		}
		if (target.getAddress().getAddress().getHostAddress()
				.equals(member.getPlayer().getAddress().getAddress().getHostAddress())) {
			member.message("You can't duel a player who's on the same ip address.");
			return;
		}
		if (kitManager.getPlayerDuel().containsKey(member.getUUID())) {
			member.message("You already challenged someone for a duel.");
			return;
		}
		
		target.sendMessage("§a" + member.getName() + "§7 wants to duel you with the §a" + kit.getName() + " §7kit, type /duel accept to start the duel.");
		member.message("You challenged " + target.getName() + " §7for a duel.");
		
		kitManager.getPlayerDuel().put(target.getUniqueId(), member.getUUID());
		
		Bukkit.getScheduler().runTaskLater(getModule().getPlugin(), () -> {
			if (!inDuel(target)) {
				target.sendMessage("§7You didn't accept the duel in time.");
				kitManager.getPlayerDuel().remove(target.getUniqueId());
			}
			if (!inDuel(member.getPlayer())) {
				member.getPlayer().sendMessage("§7Your duel didn't get accepted in time.");
				kitManager.getPlayerDuel().remove(member.getUUID());
			}
		}, 30 * 20);
		kitManager.getDuelKit().put(member.getUUID(), kit);
	}
	
	private IOption<Kit> getKit(String[] args) {
	    if (args.length < 2)
	        return new None<>();
		for (Map.Entry<KitType, Kit> entry : getModule().getKits().entrySet())
			if (entry.getValue().getName().equalsIgnoreCase(args[1]))
				return new Some<>(entry.getValue());
		return new None<>();
	}
	
	private boolean inDuel(Player p) {
		for (Duel duel : kitManager.getDuels())
			if (duel.getPlayers().contains(p.getUniqueId())) 
				return true;
		return false;
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e) {
	    Player p = e.getPlayer();
	    if (inDuel(p)) {
	        List<UUID> players = getDuel(p).getPlayers();
	        Player p2 = players.get(0).equals(p.getUniqueId()) ? Bukkit.getPlayer(players.get(1)) : Bukkit.getPlayer(players.get(0));
	        if (p2 != null) {
	            p2.sendMessage("§a" +  p.getName() + "§7 left the duel.");
	            removeFromDuel(p2);
	            tpToSpawn(p2);
	        }
	    }
	}
	
	@EventHandler
	public void on(PlayerDeathEvent e) {
		Player p = e.getEntity();
		
		if (inDuel(p)) {
		    List<UUID> players = getDuel(p).getPlayers();
		    Player won = players.get(0).equals(p.getUniqueId()) ? Bukkit.getPlayer(players.get(1)) : Bukkit.getPlayer(players.get(0));
			Duel duel = getDuel(won);
			if (duel == null) {
			    removeFromDuel(p);
			    tpToSpawn(p);
			    return;
			}
			
		    double hitPercentage = UtilMath.percentage(duel.getHitCounter1(), duel.getHitCounter1() + duel.getHitCounter2());
		    if (hitPercentage < 15 || hitPercentage > 85) {
		        won.sendMessage("§7The fight was unfair, you got no rewards.");
		    } else {
		        final char heart = '\u2764';
		        Bukkit.getOnlinePlayers().forEach(pl -> pl.sendMessage("§a" + won.getName() + " §7won a duel against §a" +
		                p.getName() + "§7 with §a" +  (won.getHealth() / 2  == 0 ? 1 : (int) won.getHealth() / 2) + "§c " + heart + "§7."));
	            getModule().getKitPvP().getStats(won).setDuels(getModule().getKitPvP().getStats(won).getDuels() + 1);
		    }
		    duel.resetCounters();
	    
			tpToSpawn(p);
			tpToSpawn(won);
			kitManager.getLastPlayerFought().put(p.getUniqueId(), won.getUniqueId());
			kitManager.getLastPlayerFought().put(won.getUniqueId(), p.getUniqueId());
			checkAch(won, getModule().getKitPvP().getStats(won));
			removeFromDuel(p);
			removeFromDuel(won);
		}
	}
	
	@EventHandler
	public void on(EntityDamageByEntityEvent e) {
	    if (!(e.getEntity() instanceof Player && e.getDamager() instanceof Player))
	        return;
	    Duel duel = getDuel((Player) e.getDamager());
	    if (duel == null)
	        return;
        if (duel.getPlayers().get(0).equals(e.getDamager().getUniqueId()))
            duel.incCounter1();
        else 
            duel.incCounter2();
	}
	
	private Duel getDuel(Player p) {
	    for (Duel duel : kitManager.getDuels()) 
	        if (duel.getPlayers().contains(p.getUniqueId()))
	            return duel;
	    return null;
	}
	
	private void removeFromDuel(Player p) {
	    for (Duel duel : kitManager.getDuels()) {
	        if (duel.getPlayers().contains(p.getUniqueId())) {
	            duel.setRunning(false);
	            duel.getPlayers().remove(p.getUniqueId());
	        }
	    }
		kitManager.getPlayerDuel().remove(p.getUniqueId());
	}
	
	private void checkAch(Player p, KitPvPStats stats) {
		int duels = stats.getDuels();
		if (Achievement.DUEL.getValues().contains(duels)) {
			p.sendMessage("§aAchievement §7unlocked: win " + duels + " §aduels§7. You earned a §acrate key§7.");
			stats.setDuels(duels + 1);
			stats.setCrateKeys(stats.getCrateKeys() + 1);
		}
	}
	
	private void tpToSpawn(Player p) {
	    UtilPlayer.reset(p);
		p.teleport(getModule().getKitPvP().getSpawnpoint());
		getModule().getKitPvP().getStats(p).setActiveKit(KitType.DEFAULT);
		getModule().getKits().get(KitType.DEFAULT).applyKit(p);
		getModule().getKitPvP().getStats(p).getKitPreference().getKit().giveSoup(p, 32);
	}
	
	private void giveKit(Player p, Kit kit) {
		UtilPlayer.reset(p);
		kit.applyKit(p, 3);
		kit.giveSoup(p, 32);
		getModule().getKitPvP().getStats(p).setActiveKit(kit.getKitType());
	}


}