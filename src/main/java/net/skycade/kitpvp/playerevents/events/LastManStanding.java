package net.skycade.kitpvp.playerevents.events;

import net.skycade.crates.CratesPlugin;
import net.skycade.crates.crates.Crate;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
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
import java.util.UUID;

public class LastManStanding implements Listener {

    public ArrayList<UUID> players = new ArrayList<>();

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
                if (getEventManager().isSpectating(p)) {
                    getEventManager().removeSpectator(p);
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
                Location arena = getArenaSpawnLocation();
                p.teleport(new Location(arena.getWorld(), arena.getX(), arena.getY(), arena.getZ(), arena.getYaw(), arena.getPitch()));
            }
        }

        for (Player p: Bukkit.getOnlinePlayers()){
            if (isPlaying(p) || getEventManager().isSpectating(p)){
                Messages.LMS_STARTED.msg(p);
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                setFighting(true);
                for (Player p: Bukkit.getOnlinePlayers()){
                    if (isPlaying(p) || getEventManager().isSpectating(p)){
                        Messages.LMS_FIGHT_ENABLED.msg(p);
                    }
                }
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isPlaying(p)) {
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
        setFighting(false);
        getEventManager().setHoster(null);
    }

    public void join(Player p) {
        applyChosenKit(p);
        addPlayer(p);

        Location lobby = getLobbyLocation();
        p.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));


        for (Player player : Bukkit.getOnlinePlayers()){
            if (isPlaying(player) || getEventManager().isSpectating(player)) {
                Messages.EVENT_JOINED.msg(player, "%player%", p.getName(), "%size%", String.valueOf(getPlayers().size()));
            }
        }
    }

    @EventHandler
    public void quitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (getEventManager().getCurrentEvent() == EventType.LMS) {
            if (isPlaying(player)) {
                removePlayer(player);

                KitPvPStats stats1 = KitPvP.getInstance().getStats(player);
                stats1.setKitPreference(KitType.DUBSTEP);
                stats1.setActiveKit(KitType.DUBSTEP);

                for (Player p : Bukkit.getOnlinePlayers()){
                    if (isPlaying(player) || getEventManager().isSpectating(player)) {
                        Messages.EVENT_LEFT.msg(p, "%player%", player.getName(), "%size%", String.valueOf(getPlayers().size()));
                    }
                }

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                if (isFighting()) {
                    if (getPlayers().size() == 1) {
                        Player theWinner = Bukkit.getPlayer(getPlayers().get(0));
                        for (Player onlineP : Bukkit.getOnlinePlayers()) {
                            if (isPlaying(onlineP)) {
                                removePlayer(onlineP);
                                getEventManager().removeFromEvent(onlineP);
                            }
                            if (getEventManager().isSpectating(onlineP)) {
                                getEventManager().removeSpectator(onlineP);
                            }
                        }
                        String winner = Messages.WON_EVENT.getMessage();
                        winner = winner.replaceAll("%player%", theWinner.getName()).replaceAll("%event%", "Last Man Standing");
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                        end();
                        getEventManager().rewardPlayer(theWinner);
                        return;
                    }
                }
            }
            getEventManager().removeSpectator(player);
            Location spawn = KitPvP.getInstance().getSpawnLocation();
            player.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
        }
    }


    public void quit(Player p) {
        if (isPlaying(p)) {
            removePlayer(p);

            for (Player player : Bukkit.getOnlinePlayers()){
                if (isPlaying(player) || getEventManager().isSpectating(player)) {
                    Messages.EVENT_LEFT.msg(player, "%player%", p.getName(), "%size%", String.valueOf(getPlayers().size()));
                }
            }

            Location spawn = KitPvP.getInstance().getSpawnLocation();
            p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
            if (isFighting()) {
                if (getPlayers().size() == 1) {
                    Player theWinner = Bukkit.getPlayer(getPlayers().get(0));
                    for (Player onlineP : Bukkit.getOnlinePlayers()) {
                        if (isPlaying(onlineP)) {
                            removePlayer(onlineP);
                            getEventManager().removeFromEvent(onlineP);
                        }
                        if (getEventManager().isSpectating(onlineP)) {
                            getEventManager().removeSpectator(onlineP);
                        }
                    }
                    String winner = Messages.WON_EVENT.getMessage();
                    winner = winner.replaceAll("%player%", theWinner.getName()).replaceAll("%event%", "Last Man Standing");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                    end();
                    getEventManager().rewardPlayer(theWinner);
                    return;
                }
            }
        }
        getEventManager().removeSpectator(p);
    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            if (isPlaying(player) && isPlaying(killer)) {
                player.spigot().respawn();
                removePlayer(player);

                for (ItemStack armor : killer.getInventory().getArmorContents()) {
                    if (armor != null) {
                        armor.setDurability((short) (armor.getDurability() - UtilMath.getRandom(5, 10)));
                    }
                }

                for (Player p : Bukkit.getOnlinePlayers()){
                    if (isPlaying(p) || getEventManager().isSpectating(p)) {
                        Messages.LMS_ELIMINATED.msg(p, "%player%", player.getName(), "%killer%", killer.getName(), "%remaining%", String.valueOf(getPlayers().size()));
                    }
                }

                giveSoup(killer, 14);
                if (getPlayers().size() == 1) {
                    for (Player onlineP : Bukkit.getOnlinePlayers()) {
                        if (isPlaying(onlineP)) {
                            removePlayer(onlineP);
                            getEventManager().removeFromEvent(onlineP);
                        }
                        if (getEventManager().isSpectating(onlineP)) {
                            getEventManager().removeSpectator(onlineP);
                        }
                    }
                    Messages.WON_EVENT.broadcast("%player%", killer.getName(), "%event%", "Last Man Standing");
                    end();
                    getEventManager().rewardPlayer(killer);
                    return;
                }
                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                stats.setKitPreference(KitType.DUBSTEP);
                stats.setActiveKit(KitType.DEFAULT);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        Location lobby = getLobbyLocation();
                        player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), lobby.getYaw(), lobby.getPitch()));

                        new BukkitRunnable(){
                                    @Override
                                    public void run() {
                                        getEventManager().addSpectator(player);
                                    }
                                }.runTaskLater(KitPvP.getInstance(), 2);

                    }
                }.runTaskLater(KitPvP.getInstance(), 5);
            }
        }

        if (isPlaying(player)){
            player.spigot().respawn();
            removePlayer(player);

            for (Player p : Bukkit.getOnlinePlayers()){
                if (isPlaying(player) || getEventManager().isSpectating(player)) {
                    Messages.LMS_ELIMINATED.msg(p, "%player%", player.getName(), "%killer%", "Unknown", "%size%", String.valueOf(getPlayers().size()));
                }
            }

            KitPvPStats stats = KitPvP.getInstance().getStats(player);
            stats.setKitPreference(KitType.DUBSTEP);
            stats.setActiveKit(KitType.DEFAULT);
            new BukkitRunnable() {

                @Override
                public void run() {

                    Location ploc = player.getLocation();
                    Location lobby = getLobbyLocation();
                    player.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), ploc.getYaw(), ploc.getPitch()));

                    UtilPlayer.reset(player);
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            getEventManager().addSpectator(player);
                        }
                    }.runTaskLater(KitPvP.getInstance(), 2);
                }
            }.runTaskLater(KitPvP.getInstance(), 5);

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
    public void blockCommands(PlayerCommandPreprocessEvent event) {
        if (getEventManager().getCurrentEvent() == EventType.LMS) {
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
    public void hit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (getEventManager().getCurrentEvent() == EventType.LMS) {
                    Player victim = (Player) event.getEntity();
                    Player attacker = (Player) event.getDamager();

                    if (getEventManager().isSpectating(victim) || getEventManager().isSpectating(attacker)) {
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
                if (getEventManager().isSpectating(victim)) {
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

    public boolean isFighting() {
        return fighting;
    }

    public void setFighting(boolean fighting) {
        this.fighting = fighting;
    }

    public ArrayList<UUID> getPlayers() {
        return this.players;
    }

    public void setLobbyLocation(Location loc) {
        KitPvP.getInstance().getConfig().set("player-events.lms.lobby-location", loc);
        KitPvP.getInstance().saveConfig();
    }

    public void setArenaLocation(Location loc) {
        KitPvP.getInstance().getConfig().set("player-events.lms.arena-location", loc);
        KitPvP.getInstance().saveConfig();
    }

    public void setRegionPosition(Location loc, int position) {
        KitPvP.getInstance().getConfig().set("player-events.lms.region.pos" + position, loc);
        KitPvP.getInstance().saveConfig();
    }

}
