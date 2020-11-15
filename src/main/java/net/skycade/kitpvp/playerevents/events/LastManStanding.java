package net.skycade.kitpvp.playerevents.events;

import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.playerevents.EventManager;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import java.util.UUID;

public class LastManStanding implements Listener {

    public ArrayList<UUID> players = new ArrayList<>();
    public ArrayList<UUID> spectators = new ArrayList<>();

    public KitType chosenKit;

    public boolean fighting;

    public LastManStanding() {
        Bukkit.getPluginManager().registerEvents(this, KitPvP.getInstance());
        setFighting(false);
    }

    public void start() {
        if (getPlayers().size() < 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (isPlaying(p)) {
                    removePlayer(p);
                    Location spawn = KitPvP.getInstance().getSpawnLocation();
                    p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                    Messages.EVENT_FAILED_LACK_OF_PLAYERS.msg(p);
                }
                if (isSpectating(p)) {
                    removeSpectator(p);
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

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uuid = p.getUniqueId();
            if (getPlayers().contains(uuid)) {
                Location ploc = p.getLocation();
                Location arena = getArenaSpawnLocation();
                p.teleport(new Location(arena.getWorld(), arena.getX(), arena.getY(), arena.getZ(), ploc.getYaw(), ploc.getPitch()));
            }
        }
        String message = Messages.LMS_STARTED.getMessage();
        sendMessageToPlayers(message);
        sendMessageToSpectators(message);

        new BukkitRunnable() {

            @Override
            public void run() {
                setFighting(true);
                String message = Messages.LMS_FIGHT_ENABLED.getMessage();
                sendMessageToPlayers(message);
                sendMessageToSpectators(message);
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 5);
    }

    public void end() {
        getEventManager().setGlobalCooldown(System.currentTimeMillis() + ((5 * 60) * 1000L));
        getEventManager().setCooldownOn(true);
        getEventManager().setCurrentEvent(EventType.IDLE);
        setFighting(false);
        getEventManager().setHoster(null);
    }

    public void forceEnd() {
        sendMessageToPlayers("&cThis event has been forcefully ended by an Administrator.");
        sendMessageToSpectators("&cThis event has been forcefully ended by an Administrator.");

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isPlaying(p)) {
                removePlayer(p);
                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                stats.setKitPreference(KitType.CHANCE);
                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                stats.applyKitPreference();
            }
            if (isSpectating(p)) {
                removeSpectator(p);
                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                stats.setKitPreference(KitType.CHANCE);
                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                stats.applyKitPreference();
            }
        }

        getEventManager().setCurrentEvent(EventType.IDLE);
        setFighting(false);
        getEventManager().setHoster(null);
    }

    public void join(Player p) {
        applyChosenKit(p);
        addPlayer(p);

        Location ploc = p.getLocation();
        Location lobby = getLobbyLocation();

        p.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), ploc.getYaw(), ploc.getPitch()));

        String joinmsg = Messages.EVENT_JOINED.getMessage();
        joinmsg = joinmsg.replaceAll("%player%", p.getName()).
                replaceAll("%size%", String.valueOf(getPlayers().size()));

        sendMessageToPlayers(joinmsg);
        sendMessageToSpectators(joinmsg);
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (getEventManager().getCurrentEvent() == EventType.LMS) {
            if (isPlaying(player)) {
                removePlayer(player);

                String quitmsg = Messages.EVENT_LEFT.getMessage();
                quitmsg = quitmsg.replaceAll("%player%", player.getName())
                        .replaceAll("%size%", String.valueOf(getPlayers().size()));

                sendMessageToPlayers(quitmsg);
                sendMessageToSpectators(quitmsg);

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                if (isFighting()) {
                    if (getPlayers().size() == 1) {
                        Player theWinner = Bukkit.getPlayer(getPlayers().get(0));
                        for (Player onlineP : Bukkit.getOnlinePlayers()) {
                            if (isPlaying(onlineP)) {
                                removePlayer(onlineP);
                                KitPvPStats stats = KitPvP.getInstance().getStats(onlineP);
                                stats.setKitPreference(KitType.CHANCE);
                                onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                                stats.applyKitPreference();
                            }
                            if (isSpectating(onlineP)) {
                                removeSpectator(onlineP);
                                KitPvPStats stats = KitPvP.getInstance().getStats(onlineP);
                                stats.setKitPreference(KitType.CHANCE);
                                onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                                stats.applyKitPreference();
                            }
                        }
                        String winner = Messages.WON_EVENT.getMessage();
                        winner = winner.replaceAll("%player%", theWinner.getName()).replaceAll("%event%", "Last Man Standing");
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                        end();
                        return;
                    }
                }
            }
            removeSpectator(player);
            Location spawn = KitPvP.getInstance().getSpawnLocation();
            player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
        }
    }


    public void quit(Player p) {
        if (isPlaying(p)) {
            removePlayer(p);

            String quitmsg = Messages.EVENT_LEFT.getMessage();
            quitmsg = quitmsg.replaceAll("%player%", p.getName())
                    .replaceAll("%size%", String.valueOf(getPlayers().size()));

            sendMessageToPlayers(quitmsg);
            sendMessageToSpectators(quitmsg);

            Location spawn = KitPvP.getInstance().getSpawnLocation();
            p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            if (isFighting()) {
                if (getPlayers().size() == 1) {
                    Player theWinner = Bukkit.getPlayer(getPlayers().get(0));
                    for (Player onlineP : Bukkit.getOnlinePlayers()) {
                        if (isPlaying(onlineP)) {
                            removePlayer(onlineP);
                            KitPvPStats stats = KitPvP.getInstance().getStats(onlineP);
                            stats.setKitPreference(KitType.CHANCE);
                            onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                            stats.applyKitPreference();
                        }
                        if (isSpectating(onlineP)) {
                            removeSpectator(onlineP);
                            KitPvPStats stats = KitPvP.getInstance().getStats(onlineP);
                            stats.setKitPreference(KitType.CHANCE);
                            onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                            stats.applyKitPreference();
                        }
                    }
                    String winner = Messages.WON_EVENT.getMessage();
                    winner = winner.replaceAll("%player%", theWinner.getName()).replaceAll("%event%", "Last Man Standing");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                    end();
                    return;
                }
            }
        }
        removeSpectator(p);
        Location spawn = KitPvP.getInstance().getSpawnLocation();
        p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            if (isPlaying(player) && isPlaying(killer)) {
                player.spigot().respawn();
                removePlayer(player);
                addSpectator(player);

                String deathmsg = Messages.LMS_ELIMINATED.getMessage();
                deathmsg = deathmsg.replaceAll("%player%", player.getName())
                        .replaceAll("%killer%", killer.getName())
                        .replaceAll("%remaining%", String.valueOf(getPlayers().size()));

                sendMessageToPlayers(deathmsg);
                sendMessageToSpectators(deathmsg);

                giveSoup(killer, 14);
                if (getPlayers().size() == 1) {
                    for (Player onlineP : Bukkit.getOnlinePlayers()) {
                        if (isPlaying(onlineP)) {
                            removePlayer(onlineP);
                            KitPvPStats stats = KitPvP.getInstance().getStats(onlineP);
                            stats.setKitPreference(KitType.CHANCE);
                            Location spawn = KitPvP.getInstance().getSpawnLocation();
                            onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                            stats.applyKitPreference();
                        }
                        if (isSpectating(onlineP)) {
                            removeSpectator(onlineP);
                            KitPvPStats stats = KitPvP.getInstance().getStats(onlineP);
                            stats.setKitPreference(KitType.CHANCE);
                            Location spawn = KitPvP.getInstance().getSpawnLocation();
                            onlineP.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                            stats.applyKitPreference();
                        }
                    }
                    String winner = Messages.WON_EVENT.getMessage();
                    winner = winner.replaceAll("%player%", killer.getName()).replaceAll("%event%", "Last Man Standing");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                    end();
                    return;
                }
                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                stats.setKitPreference(KitType.CHANCE);
                stats.setActiveKit(KitType.DEFAULT);
                new BukkitRunnable() {

                    @Override
                    public void run() {

                        Location ploc = player.getLocation();
                        Location lobby = getLobbyLocation();
                        player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), ploc.getYaw(), ploc.getPitch()));

                        player.getInventory().clear();
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                        player.getInventory().setChestplate(new ItemStack(Material.AIR));
                        player.getInventory().setLeggings(new ItemStack(Material.AIR));
                        player.getInventory().setBoots(new ItemStack(Material.AIR));

                        for (PotionEffect effect : player.getActivePotionEffects()) {
                            player.removePotionEffect(effect.getType());
                        }
                    }
                }.runTaskLater(KitPvP.getInstance(), 5);
            }
        }
    }

    private void applyChosenKit(Player p) {
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        if (getChosenKit() == KitType.KNIGHT) {
            stats.setActiveKit(KitType.KNIGHT);
            KitKnight kit = new KitKnight(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 36);
            return;
        }
        if (getChosenKit() == KitType.REAPER) {
            stats.setActiveKit(KitType.REAPER);
            KitReaper kit = new KitReaper(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 36);
            return;
        }
        if (getChosenKit() == KitType.PAINTBALL) {
            stats.setActiveKit(KitType.PAINTBALL);
            KitPaintball kit = new KitPaintball(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 36);
            return;
        }
        if (getChosenKit() == KitType.BUILDUHC) {
            stats.setActiveKit(KitType.BUILDUHC);
            KitBuildUHC kit = new KitBuildUHC(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 36);
            return;
        }
        if (getChosenKit() == KitType.DUBSTEP) {
            stats.setActiveKit(KitType.DUBSTEP);
            KitDubstep kit = new KitDubstep(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 36);
            return;
        }
        if (getChosenKit() == KitType.TELEPORTER) {
            stats.setActiveKit(KitType.TELEPORTER);
            KitTeleporter kit = new KitTeleporter(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 36);
        }
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
    public void blockCommands(PlayerCommandPreprocessEvent event) {
        if (getEventManager().getCurrentEvent() == EventType.LMS) {
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

    @EventHandler
    public void hit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (getEventManager().getCurrentEvent() == EventType.LMS) {
                    Player victim = (Player) event.getEntity();
                    Player attacker = (Player) event.getDamager();

                    if (isSpectating(victim) || isSpectating(attacker)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (isPlaying(victim) || isPlaying(attacker)) {
                        if (!isFighting()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
            if (event.getDamager() instanceof Projectile) {
                Player victim = (Player) event.getEntity();
                if (isSpectating(victim)) {
                    event.setCancelled(true);
                }
                if (isPlaying(victim) && !isFighting()) {
                    event.setCancelled(true);
                }
            }
        }
    }


    public void sendMessageToPlayers(String message) {
        if (this.players == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlaying(player)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public void sendMessageToSpectators(String message) {
        if (this.spectators == null) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isSpectating(p)) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    public Location getLobbyLocation() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.lms.lobby-location");
    }

    public Location getArenaSpawnLocation() {
        return (Location) KitPvP.getInstance().getConfig().get("player-events.lms.arena-location");
    }

    public KitType getChosenKit() {
        return chosenKit;
    }

    public void setChosenKit(KitType chosenKit) {
        this.chosenKit = chosenKit;
    }

    private EventManager getEventManager() {
        return KitPvP.getInstance().getEventManager();
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

    public void removePlayer(Player p) {
        ArrayList<UUID> list = getPlayers();
        list.remove(p.getUniqueId());

        this.players = list;
    }

    public void removeSpectator(Player p) {
        ArrayList<UUID> list = getSpectators();
        list.remove(p.getUniqueId());

        this.spectators = list;
    }

    public boolean isSpectating(Player p) {
        if (this.spectators == null || this.spectators.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.spectators;
        return list.contains(p.getUniqueId());
    }

    public boolean isPlaying(Player p) {
        if (this.players == null || this.players.isEmpty()) {
            return false;
        }
        ArrayList<UUID> list = this.players;
        return list.contains(p.getUniqueId());
    }

    public boolean isFighting() {
        return fighting;
    }

    public void setFighting(boolean fighting) {
        this.fighting = fighting;
    }

    public ArrayList<UUID> getPlayers() {
        return this.players;
    }


    public ArrayList<UUID> getSpectators() {
        return this.spectators;
    }

    public void setLobbyLocation(Location loc) {
        KitPvP.getInstance().getConfig().set("player-events.lms.lobby-location", loc);
        KitPvP.getInstance().saveConfig();
    }

    public void setArenaLocation(Location loc) {
        KitPvP.getInstance().getConfig().set("player-events.lms.arena-location", loc);
        KitPvP.getInstance().saveConfig();
    }

}
