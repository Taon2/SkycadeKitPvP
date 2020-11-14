package net.skycade.kitpvp.playerevents.events;

import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class SumoEvent implements Listener {

    public ArrayList<UUID> participants = new ArrayList<>();
    public ArrayList<UUID> allPlayers = new ArrayList<>();
    public ArrayList<UUID> spectators = new ArrayList<>();
    public ArrayList<UUID> fightingPlayers = new ArrayList<>();

    private boolean countdown;

    public SumoEvent() {
        Bukkit.getPluginManager().registerEvents(this, KitPvP.getInstance());
    }

    public Location getLobbyLocation() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.sumo.lobby-location");
    }

    public Location getFighter1Position() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.sumo.position-1");
    }

    public Location getFighter2Position() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.sumo.position-2");
    }

    public void join(Player p) {
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        stats.setKitPreference(KitType.CHANCE);
        stats.setActiveKit(KitType.DEFAULT);

        p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.getInventory().setLeggings(new ItemStack(Material.AIR));
        p.getInventory().setBoots(new ItemStack(Material.AIR));

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        addParticipant(p);
        addPlayer(p);

        Location loc = p.getLocation();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        Location lobby = getLobbyLocation();
        p.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), yaw, pitch));

        String joinmsg = Messages.EVENT_JOINED.getMessage();
        joinmsg = joinmsg.replaceAll("%player%", p.getName()).replaceAll("%size%", String.valueOf(getPlayers().size()));

        sendMessageToPlayers(joinmsg);
        sendMessageToSpectators(joinmsg);
    }

    public void leaveEvent(Player p){
        if (getEventManager().getCurrentEvent() == EventType.SUMO) {
            if (isParticipating(p)){
                removeParticipant(p);
                removePlayer(p);

                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                stats.setKitPreference(KitType.CHANCE);
                stats.setActiveKit(KitType.CHANCE);

                String leavemsg = Messages.EVENT_LEFT.getMessage();
                leavemsg = leavemsg.replaceAll("%player%", p.getName())
                        .replaceAll("%size%", String.valueOf(getPlayers().size()));

                sendMessageToSpectators(leavemsg);
                sendMessageToPlayers(leavemsg);

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            }
            if (isSpectating(p)){
                removeSpectator(p);

                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                stats.setKitPreference(KitType.CHANCE);
                stats.setActiveKit(KitType.CHANCE);

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            }

            if (isFighting(p)){
                removePlayer(p);
                removeFighter(p);

                Player opponent = Bukkit.getPlayer(getFightingPlayers().get(0));
                Location loc = opponent.getLocation();
                Location lobby = getLobbyLocation();

                opponent.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), loc.getYaw(), loc.getPitch()));
                removeFighter(opponent);
                addParticipant(opponent);

                String eliminated = Messages.SUMO_ELIMINATED.getMessage();
                eliminated = eliminated.replaceAll("%player%", p.getName());

                sendMessageToPlayers(eliminated);
                sendMessageToSpectators(eliminated);

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
                        Location spawn = KitPvP.getInstance().getSpawnLocation();
                        onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                    }
                    if (isSpectating(onlineP)) {
                        removeSpectator(onlineP);
                        Location spawn = KitPvP.getInstance().getSpawnLocation();
                        onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                    }
                }
                String winner = Messages.WON_EVENT.getMessage();
                winner = winner.replaceAll("%player%", opponent.getName()).replaceAll("%event%", "Sumo");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                end();
            }
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (getEventManager().getCurrentEvent() == EventType.SUMO) {
            if (isParticipating(player)){
                removeParticipant(player);
                removePlayer(player);

                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                stats.setKitPreference(KitType.CHANCE);
                stats.setActiveKit(KitType.CHANCE);

                String leavemsg = Messages.EVENT_LEFT.getMessage();
                leavemsg = leavemsg.replaceAll("%player%", player.getName())
                        .replaceAll("%size%", String.valueOf(getPlayers().size()));

                sendMessageToSpectators(leavemsg);
                sendMessageToPlayers(leavemsg);

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            }
            if (isSpectating(player)){
                removeSpectator(player);

                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                stats.setKitPreference(KitType.CHANCE);
                stats.setActiveKit(KitType.CHANCE);

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            }

            if (isFighting(player)){
                removePlayer(player);
                removeFighter(player);

                Player opponent = Bukkit.getPlayer(getFightingPlayers().get(0));
                Location loc = opponent.getLocation();
                Location lobby = getLobbyLocation();

                opponent.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), loc.getYaw(), loc.getPitch()));
                removeFighter(opponent);
                addParticipant(opponent);

                String eliminated = Messages.SUMO_ELIMINATED.getMessage();
                eliminated = eliminated.replaceAll("%player%", player.getName());

                sendMessageToPlayers(eliminated);
                sendMessageToSpectators(eliminated);

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
                        Location spawn = KitPvP.getInstance().getSpawnLocation();
                        onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                    }
                    if (isSpectating(onlineP)) {
                        removeSpectator(onlineP);
                        Location spawn = KitPvP.getInstance().getSpawnLocation();
                        onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                    }
                }
                String winner = Messages.WON_EVENT.getMessage();
                winner = winner.replaceAll("%player%", opponent.getName()).replaceAll("%event%", "Sumo");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                end();
            }
        }
    }

    public void start() {
        if (getPlayers().size() <= 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isPlaying(p) && getPlayers() != null) {
                    removePlayer(p);
                    removeParticipant(p);
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

    public void end(){
        getEventManager().setGlobalCooldown(System.currentTimeMillis() + ((5 * 60) * 1000L));
        getEventManager().setCooldownOn(true);
        getEventManager().setCurrentEvent(EventType.IDLE);
        this.countdown = false;
        getEventManager().setHoster(null);
    }

    public void forceEnd(){
        sendMessageToPlayers("&cThis event has been forcefully ended by an Administrator.");
        sendMessageToSpectators("&cThis event has been forcefully ended by an Administrator.");

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isPlaying(p)) {
                removePlayer(p);
                removeParticipant(p);
                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            }
            if (isSpectating(p)) {
                removeSpectator(p);
                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            }
        }

        getEventManager().setCurrentEvent(EventType.IDLE);
        this.countdown = false;
        getEventManager().setHoster(null);
    }



    public void startRound(Player fighter1, Player fighter2) {
        Location fighter1Loc = fighter1.getLocation();
        Location fighter2Loc = fighter2.getLocation();

        Location pos1 = getFighter1Position();
        Location pos2 = getFighter2Position();
        fighter1.teleport(new Location(pos1.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ(), fighter1Loc.getYaw(), fighter1Loc.getPitch()));
        fighter2.teleport(new Location(pos2.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ(), fighter2Loc.getYaw(), fighter2Loc.getPitch()));

        String roundstart = Messages.SUMO_ROUND_STARTED.getMessage();

        String fighters = Messages.SUMO_ROUND_FIGHTERS.getMessage();
        fighters = fighters.replaceAll("%fighter1%", fighter1.getName()).replaceAll("%fighter2%", fighter2.getName());

        sendMessageToSpectators(roundstart);
        sendMessageToSpectators(fighters);

        sendMessageToPlayers(roundstart);
        sendMessageToPlayers(fighters);

        new BukkitRunnable(){

            @Override
            public void run() {
                countdown = true;
            }
        }.runTaskLater(KitPvP.getInstance(), 1);

        new BukkitRunnable(){

            @Override
            public void run() {
                countdown = false;
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 5 + 1);
    }

    @EventHandler
    public void elimination(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (getEventManager().getCurrentEvent() == EventType.SUMO) {
            if (isFighting(player)) {
                if (countdown){
                    if (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockZ() != event.getFrom().getBlockZ()){
                        event.setTo(event.getFrom());
                    }
                    return;
                }

                Location loc1 = player.getLocation();
                if (loc1.getBlock().getType() == Material.WATER || loc1.getBlock().getType() == Material.STATIONARY_WATER) {
                    removeFighter(player);
                    removePlayer(player);
                    addSpectator(player);

                    Player opponent = Bukkit.getPlayer(getFightingPlayers().get(0));
                    Location loc = opponent.getLocation();
                    Location lobby = getLobbyLocation();

                    player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), loc.getYaw(), loc.getPitch()));
                    opponent.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), loc.getYaw(), loc.getPitch()));
                    removeFighter(opponent);
                    addParticipant(opponent);

                    String eliminated = Messages.SUMO_ELIMINATED.getMessage();
                    eliminated = eliminated.replaceAll("%player%", player.getName());

                    sendMessageToPlayers(eliminated);
                    sendMessageToSpectators(eliminated);
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
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (isPlaying(p)) {
                            removePlayer(p);
                            removeParticipant(p);
                            Location spawn = KitPvP.getInstance().getSpawnLocation();
                            p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                        }
                        if (isSpectating(p)) {
                            removeSpectator(p);
                            Location spawn = KitPvP.getInstance().getSpawnLocation();
                            p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                        }
                    }
                    String winner = Messages.WON_EVENT.getMessage();
                    winner = winner.replaceAll("%player%", opponent.getName()).replaceAll("%event%", "Sumo");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                    end();
                }
            }
        }
    }

    @EventHandler
    public void attack(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player){
            if (event.getDamager() instanceof Player) {
                Player victim = (Player) event.getEntity();
                Player attacker = (Player) event.getDamager();
                if (getEventManager().getCurrentEvent() == EventType.SUMO) {
                    if (isParticipating(victim)){
                        event.setCancelled(true);
                    }
                    if (isParticipating(attacker)){
                        event.setCancelled(true);
                    }
                    if (isSpectating(victim)){
                        event.setCancelled(true);
                    }
                    if (isSpectating(attacker)){
                        event.setCancelled(true);
                    }
                    if (isFighting(attacker) && isFighting(attacker)) {
                        event.setDamage(0);
                    }
                }
            }
        }
    }

    @EventHandler
    public void blockCommands(PlayerCommandPreprocessEvent event){
        if (getEventManager().getCurrentEvent() == EventType.SUMO){
            Player player = event.getPlayer();
            if (isPlaying(player) || isSpectating(player)) {
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

    public void sendMessageToParticipants(String message) {
        if (this.participants == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.participants.contains(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public void sendMessageToPlayers(String message) {
        if (this.allPlayers == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlaying(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
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

    public void addPlayer(Player p) {
        if (this.allPlayers == null || this.allPlayers.isEmpty()) {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(p.getUniqueId());

            this.allPlayers = list;
            return;
        }
        ArrayList<UUID> list = this.allPlayers;
        list.add(p.getUniqueId());

        this.allPlayers = list;
    }

    public void addFighter(Player p) {
        if (this.fightingPlayers == null || this.fightingPlayers.isEmpty()) {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(p.getUniqueId());

            this.fightingPlayers = list;
            return;
        }
        ArrayList<UUID> list = this.fightingPlayers;
        list.add(p.getUniqueId());

        this.fightingPlayers = list;
    }

    public void addSpectator(Player p) {
        if (this.spectators == null || this.spectators.isEmpty()) {
            ArrayList<UUID> list = new ArrayList<>();
            list.add(p.getUniqueId());

            this.spectators = list;
            return;
        }
        ArrayList<UUID> list = this.spectators;
        list.add(p.getUniqueId());

        this.spectators = list;
    }

    public void removeParticipant(Player p) {
        ArrayList<UUID> list = getParticipants();
        list.remove(p.getUniqueId());

        this.participants = list;
    }

    public void removePlayer(Player p) {
        ArrayList<UUID> list = getPlayers();
        list.remove(p.getUniqueId());

        this.allPlayers = list;
    }

    public void removeFighter(Player p) {
        ArrayList<UUID> list = getFightingPlayers();
        list.remove(p.getUniqueId());

        this.fightingPlayers = list;
    }

    public void removeSpectator(Player p) {
        ArrayList<UUID> list = getSpectators();
        list.remove(p.getUniqueId());

        this.spectators = list;
    }

    public boolean isFighting(Player p) {
        if (this.fightingPlayers == null || this.fightingPlayers.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.fightingPlayers;
        return list.contains(p.getUniqueId());
    }

    public boolean isSpectating(Player p) {
        if (this.spectators == null || this.spectators.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.spectators;
        return list.contains(p.getUniqueId());
    }

    public boolean isParticipating(Player p) {
        if (this.participants == null || this.participants.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.participants;
        return list.contains(p.getUniqueId());
    }

    public boolean isPlaying(Player p) {
        if (this.allPlayers == null || this.allPlayers.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.allPlayers;
        return list.contains(p.getUniqueId());
    }

    public ArrayList<UUID> getParticipants() {
        return this.participants;
    }

    public ArrayList<UUID> getPlayers() {
        return this.allPlayers;
    }

    public ArrayList<UUID> getFightingPlayers() {
        return this.fightingPlayers;
    }

    public ArrayList<UUID> getSpectators() {
        return this.spectators;
    }

    public void sendMessageToSpectators(String message) {
        if (this.spectators == null) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isSpectating(p)) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    private EventManager getEventManager() {
        return KitPvP.getInstance().getEventManager();
    }
}
