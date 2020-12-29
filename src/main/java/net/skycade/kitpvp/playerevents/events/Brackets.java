package net.skycade.kitpvp.playerevents.events;

import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Brackets implements Listener {

    public ArrayList<UUID> players = new ArrayList<>();
    public ArrayList<UUID> participants = new ArrayList<>();
    public ArrayList<UUID> spectators = new ArrayList<>();
    public ArrayList<UUID> fighters = new ArrayList<>();

    public KitType chosenKit;

    public Brackets() {
        Bukkit.getPluginManager().registerEvents(this, KitPvP.getInstance());
    }

    public void start() {
        if (getPlayers().size() < 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isPlaying(p)) {
                    removePlayer(p);
                    removeParticipant(p);
                    Location spawn = KitPvP.getInstance().getSpawnLocation();
                    p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                    Messages.EVENT_FAILED_LACK_OF_PLAYERS.msg(p);
                }
                if (getEventManager().isSpectating(p)) {
                    getEventManager().removeSpectator(p);
                    Location spawn = KitPvP.getInstance().getSpawnLocation();
                    p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                    Messages.EVENT_FAILED_LACK_OF_PLAYERS.msg(p);
                }
            }
            end();
            getEventManager().setGlobalCooldown(System.currentTimeMillis() + (((2 * 60) * 1000L) + (30 * 1000L)));
            getEventManager().setCooldownOn(true);
            return;
        }
        Crate crate = CratesPlugin.getInstance().getEditorModule().getCrate("hostcredit");
        crate.getKey().take(getEventManager().getHoster(), 1);

        Random random = new Random();

        Player fighter1 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
        removeParticipant(fighter1);

        Player fighter2 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
        removeParticipant(fighter2);

        addFighter(fighter1);
        addFighter(fighter2);

        startRound(fighter1, fighter2);
    }

    public void end() {
        getEventManager().setGlobalCooldown(System.currentTimeMillis() + ((5 * 60) * 1000L));
        getEventManager().setCooldownOn(true);
        getEventManager().setCurrentEvent(EventType.IDLE);
        getEventManager().setHoster(null);
    }

    public void forceEnd() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isFighting(p)){
               removeFighter(p);
               removePlayer(p);

               getEventManager().removeFromEvent(p);
               p.sendMessage(ChatColor.RED + "This event has been forcefully ended by an Administrator.");
            }
            if (isPlaying(p)) {
                removeParticipant(p);
                removePlayer(p);

                getEventManager().removeFromEvent(p);
                p.sendMessage(ChatColor.RED + "This event has been forcefully ended by an Administrator.");
            }
            if (getEventManager().isSpectating(p)) {
                getEventManager().removeSpectator(p);
                p.sendMessage(ChatColor.RED + "This event has been forcefully ended by an Administrator.");
            }
        }
        getEventManager().setCurrentEvent(EventType.IDLE);
        getEventManager().setHoster(null);
    }

    public void join(Player p) {
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        stats.setKitPreference(KitType.CHANCE);
        stats.setActiveKit(KitType.DEFAULT);

        UtilPlayer.reset(p);

        addParticipant(p);
        addPlayer(p);

        Location lobby = getLobbyLocation();
        p.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));

        for (Player player : Bukkit.getOnlinePlayers()){
            if (isPlaying(player) || getEventManager().isSpectating(player)){
                Messages.EVENT_JOINED.msg(player, "%player%", p.getName(),
                        "%size%", String.valueOf(getPlayers().size()));
            }
        }
    }

    public void quit(Player p) {
        if (getEventManager().getCurrentEvent() == EventType.BRACKETS) {
            if (isParticipating(p)) {
                removeParticipant(p);
                removePlayer(p);

                for (Player player : Bukkit.getOnlinePlayers()){
                    if (isPlaying(player) || getEventManager().isSpectating(player)){
                        Messages.EVENT_LEFT.msg(player, "%player%", p.getName(),
                                "%size%", String.valueOf(getPlayers().size()));
                    }
                }

                getEventManager().removeFromEvent(p);
            }
            if (getEventManager().isSpectating(p)) {
                getEventManager().removeSpectator(p);
            }

            if (isFighting(p)) {
                removePlayer(p);
                removeFighter(p);

                Player opponent = Bukkit.getPlayer(getFighters().get(0));
                Location loc = opponent.getLocation();
                Location lobby = getLobbyLocation();

                opponent.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), loc.getYaw(), loc.getPitch()));
                removeFighter(opponent);
                addParticipant(opponent);

                KitPvPStats killerStats = KitPvP.getInstance().getStats(opponent);
                killerStats.setActiveKit(KitType.DEFAULT);

                UtilPlayer.reset(opponent);

                opponent.setHealth(20D);

                for (Player player : Bukkit.getOnlinePlayers()){
                    if (isPlaying(player) || getEventManager().isSpectating(player)){
                        Messages.BRACKETS_ELIMINATED.msg(player, "%player%", p.getName(),
                                "%remaining%", String.valueOf(getPlayers().size()));
                    }
                }

                if (getParticipants().size() > 1) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Random random = new Random();

                            Player fighter1 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
                            removeParticipant(fighter1);

                            Player fighter2 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
                            removeParticipant(fighter2);

                            addFighter(fighter1);
                            addFighter(fighter2);

                            startRound(fighter1, fighter2);
                        }
                    }.runTaskLater(KitPvP.getInstance(), 20 * 5);
                    return;
                }
                for (Player onlineP : Bukkit.getOnlinePlayers()) {
                    if (isPlaying(onlineP)) {
                        removePlayer(onlineP);
                        removeParticipant(onlineP);
                        getEventManager().removeFromEvent(onlineP);
                    }
                    if (getEventManager().isSpectating(onlineP)) {
                       getEventManager().removeSpectator(onlineP);
                    }
                }
                Messages.WON_EVENT.broadcast("%player%", opponent.getName(), "%event%", "Brackets");
                getEventManager().rewardPlayer(opponent);
                end();
            }
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        if (getEventManager().getCurrentEvent() == EventType.BRACKETS) {
            Player p = event.getPlayer();
            if (isParticipating(p)) {
                removeParticipant(p);
                removePlayer(p);

                for (Player player : Bukkit.getOnlinePlayers()){
                    if (isPlaying(player) || getEventManager().isSpectating(player)){
                        Messages.EVENT_LEFT.msg(player, "%player%", p.getName(),
                                "%size%", String.valueOf(getPlayers().size()));
                    }
                }
                getEventManager().removeFromEvent(p);
            }
            if (getEventManager().isSpectating(p)) {
                getEventManager().removeSpectator(p);
            }

            if (isFighting(p)) {
                removePlayer(p);
                removeFighter(p);

                Player opponent = Bukkit.getPlayer(getFighters().get(0));
                Location lobby = getLobbyLocation();

                opponent.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));
                removeFighter(opponent);
                addParticipant(opponent);

                UtilPlayer.reset(opponent);

                KitPvPStats killerStats = KitPvP.getInstance().getStats(opponent);
                killerStats.setActiveKit(KitType.DEFAULT);

                opponent.setHealth(20D);

                for (Player player : Bukkit.getOnlinePlayers()){
                    if (isPlaying(player) || getEventManager().isSpectating(player)){
                        Messages.BRACKETS_ELIMINATED.msg(player, "%player%", p.getName(),
                                "%remaining%", String.valueOf(getPlayers().size()));
                    }
                }

                if (getParticipants().size() > 1) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Random random = new Random();

                            Player fighter1 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
                            removeParticipant(fighter1);

                            Player fighter2 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
                            removeParticipant(fighter2);

                            addFighter(fighter1);
                            addFighter(fighter2);

                            startRound(fighter1, fighter2);
                        }
                    }.runTaskLater(KitPvP.getInstance(), 20 * 5);
                    return;
                }
                for (Player onlineP : Bukkit.getOnlinePlayers()) {
                    if (isPlaying(onlineP)) {
                        removePlayer(onlineP);
                        removeParticipant(onlineP);
                        getEventManager().removeFromEvent(onlineP);
                    }
                    if (getEventManager().isSpectating(onlineP)) {
                        getEventManager().removeSpectator(onlineP);
                    }
                }
                Messages.WON_EVENT.broadcast("%player%", opponent.getName(), "%event%", "Brackets");
                getEventManager().rewardPlayer(opponent);
                end();
            }
        }
    }

    public void startRound(Player fighter1, Player fighter2) {

        Location pos1 = getFighter1Position();
        Location pos2 = getFighter2Position();
        fighter1.teleport(new Location(pos1.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ(), pos1.getYaw(), pos1.getPitch()));
        fighter2.teleport(new Location(pos2.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ(), pos2.getYaw(), pos2.getPitch()));

        applyChosenKit(fighter1);
        applyChosenKit(fighter2);

        for (Player p : Bukkit.getOnlinePlayers()){
            if (isPlaying(p) || getEventManager().isSpectating(p)){
                Messages.BRACKETS_ROUND_STARTED.msg(p);
                Messages.BRACKETS_ROUND_FIGHTERS.msg(p, "%fighter1%", fighter1.getName(), "%fighter2%", fighter2.getName());
            }
        }
    }

    @EventHandler
    public void blockCommands(PlayerCommandPreprocessEvent event) {
        if (getEventManager().getCurrentEvent() == EventType.BRACKETS) {
            Player player = event.getPlayer();
            if (isPlaying(player) || getEventManager().isSpectating(player)) {
                String command = event.getMessage();

                ArrayList<String> blocked = new ArrayList<>();
                blocked.add("/soup");
                blocked.add("/soup:soup");
                blocked.add("/refreshkit");
                blocked.add("/refreshkit:refreshkit");
                blocked.add("/spawn");
                blocked.add("/eventshop");
                blocked.add("/eventshop:eventshop");

                String commandlowercase = command.toLowerCase();
                if (blocked.contains(commandlowercase)) {
                    event.setCancelled(true);
                    Messages.CANNOT_USE_COMMAND.msg(player);
                }
            }
        }
    }

    @EventHandler
    public void elimination(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            if (isFighting(player) && isFighting(killer)) {
                player.spigot().respawn();
                removePlayer(player);
                removeFighter(player);

                for (Player p : Bukkit.getOnlinePlayers()){
                    if (isPlaying(p) || getEventManager().isSpectating(p)){
                        Messages.BRACKETS_ELIMINATED.msg(p, "%player%", player.getName(), "%killer%", killer.getName(),
                                "%remaining%", String.valueOf(getPlayers().size()));
                    }
                }

                removeFighter(killer);
                addParticipant(killer);
                Location lobby = getLobbyLocation();

                killer.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));

                UtilPlayer.reset(killer);

                if (getPlayers().size() == 1) {
                    for (Player onlineP : Bukkit.getOnlinePlayers()) {
                        if (isPlaying(onlineP)) {
                            removePlayer(onlineP);
                            removeFighter(onlineP);
                            removeParticipant(onlineP);

                            getEventManager().removeFromEvent(onlineP);
                        }
                        if (getEventManager().isSpectating(onlineP)) {
                            getEventManager().removeSpectator(onlineP);
                        }
                    }
                    String winner = Messages.WON_EVENT.getMessage();
                    winner = winner.replaceAll("%player%", killer.getName()).replaceAll("%event%", "Brackets");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                    getEventManager().rewardPlayer(killer);
                    end();
                    return;
                }
                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        Location lobby = getLobbyLocation();
                        player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));

                        UtilPlayer.reset(player);

                        stats.setKitPreference(KitType.DUBSTEP);
                        stats.setActiveKit(KitType.DEFAULT);

                        getEventManager().addSpectator(player);
                    }
                }.runTaskLater(KitPvP.getInstance(), 5);
                if (getParticipants().size() > 1) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Random random = new Random();

                            Player fighter1 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
                            removeParticipant(fighter1);

                            Player fighter2 = Bukkit.getPlayer(getParticipants().get(random.nextInt(getParticipants().size())));
                            removeParticipant(fighter2);

                            addFighter(fighter1);
                            addFighter(fighter2);

                            startRound(fighter1, fighter2);
                        }
                    }.runTaskLater(KitPvP.getInstance(), 20 * 5);
                }
            }
        }
    }

    private void applyChosenKit(Player p) {
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        Kit kit = getChosenKit().getKit();
        stats.setActiveKit(kit.getKitType());
        kit.beginApplyKit(p);
        giveSoup(p, 36);
    }


    private final ItemStack potion = new ItemStack(Material.POTION, 1, (short) 16421);
    private final ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP, 1);

    public void giveSoup(Player p, int amount) {
        if (p == null || !p.isOnline()) return;
        KitType activeKit = KitPvP.getInstance().getStats(p).getActiveKit();
        boolean givePotion = activeKit == KitType.POTIONMASTER
                || activeKit == KitType.BUILDUHC
                || activeKit == KitType.WITCHDOCTOR;

        // loop through the items and fill with soup
        Inventory inventory = p.getInventory();
        for (int i = 0; i < amount; i++) {
            if (inventory.firstEmpty() == -1)
                break;

            // give soup or potion where necessary
            if (givePotion)
                inventory.addItem(potion);
            else
                inventory.addItem(soup);
        }

        // set hulk's slot 1 (hand) to empty
        if (activeKit == KitType.HULK) {
            inventory.setItem(0, null);
        }
    }

    @EventHandler
    public void hit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (getEventManager().getCurrentEvent() == EventType.BRACKETS) {
                    Player victim = (Player) event.getEntity();
                    Player attacker = (Player) event.getDamager();

                    if (getEventManager().isSpectating(victim) || getEventManager().isSpectating(attacker)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (isParticipating(victim) || isParticipating(attacker)) {
                        event.setCancelled(true);
                    }
                }
            }
            if (event.getDamager() instanceof Projectile) {
                Player victim = (Player) event.getEntity();
                if (getEventManager().isSpectating(victim) || isParticipating(victim)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public KitType getChosenKit() {
        return chosenKit;
    }

    public void setChosenKit(KitType chosenKit) {
        this.chosenKit = chosenKit;
    }

    public void sendMessageToPlayers(String message) {
        if (this.players == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlaying(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public Location getLobbyLocation() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.brackets.lobby-location");
    }

    public Location getFighter1Position() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.brackets.position-1");
    }

    public Location getFighter2Position() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.brackets.position-2");
    }

    public void setRegionPosition(Location loc, int position) {
        KitPvP.getInstance().getConfig().set("player-events.brackets.region.pos" + position, loc);
        KitPvP.getInstance().saveConfig();
    }

    public void addPlayer(Player p) {
        if (this.players == null || this.players.isEmpty()) {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(p.getUniqueId());

            this.players = list;
            return;
        }
        ArrayList<UUID> list = this.players;
        list.add(p.getUniqueId());

        this.players = list;
    }

    public void removePlayer(Player p) {
        ArrayList<UUID> list = getPlayers();
        list.remove(p.getUniqueId());

        this.players = list;
    }

    public boolean isPlaying(Player p) {
        if (this.players == null || this.players.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.players;
        return list.contains(p.getUniqueId());
    }

    public boolean isFighting(Player p) {
        if (this.fighters == null || this.fighters.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.fighters;
        return list.contains(p.getUniqueId());
    }

    public void addFighter(Player p) {
        if (this.fighters == null || this.fighters.isEmpty()) {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(p.getUniqueId());

            this.fighters = list;
            return;
        }
        ArrayList<UUID> list = this.fighters;
        list.add(p.getUniqueId());

        this.fighters = list;
    }

    public void removeFighter(Player p) {
        ArrayList<UUID> list = getFighters();
        list.remove(p.getUniqueId());

        this.fighters = list;
    }

    public boolean isParticipating(Player p) {
        if (this.participants == null || this.participants.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.participants;
        return list.contains(p.getUniqueId());
    }

    public void addParticipant(Player p) {
        if (this.participants == null || this.participants.isEmpty()) {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(p.getUniqueId());

            this.participants = list;
            return;
        }
        ArrayList<UUID> list = this.participants;
        list.add(p.getUniqueId());

        this.participants = list;
    }

    public void removeParticipant(Player p) {
        ArrayList<UUID> list = getParticipants();
        list.remove(p.getUniqueId());

        this.participants = list;
    }

    public ArrayList<UUID> getParticipants() {
        return this.participants;
    }

    public ArrayList<UUID> getFighters() {
        return this.fighters;
    }

    public ArrayList<UUID> getPlayers() {
        return this.players;
    }

    private EventManager getEventManager() {
        return KitPvP.getInstance().getEventManager();
    }

    public void setLobbyLocation(Location loc) {
        KitPvP.getInstance().getConfig().set("player-events.brackets.lobby-location", loc);
        KitPvP.getInstance().saveConfig();
    }

    public void setFighter1Location(Location loc) {
        KitPvP.getInstance().getConfig().set("player-events.brackets.position-1", loc);
        KitPvP.getInstance().saveConfig();
    }

    public void setFighter2Location(Location loc) {
        KitPvP.getInstance().getConfig().set("player-events.brackets.position-2", loc);
        KitPvP.getInstance().saveConfig();
    }
}
