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
        sendMessageToPlayers("&cThis event has been forcefully ended by an Administrator.");
        sendMessageToSpectators("&cThis event has been forcefully ended by an Administrator.");

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isFighting(p)){
               removeFighter(p);
               removePlayer(p);
                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                stats.setKitPreference(KitType.CHANCE);
                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
                stats.applyKitPreference();
            }
            if (isPlaying(p)) {
                removeParticipant(p);
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
        getEventManager().setHoster(null);
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

    public void quit(Player p) {
        if (getEventManager().getCurrentEvent() == EventType.BRACKETS) {
            if (isParticipating(p)) {
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
            if (isSpectating(p)) {
                removeSpectator(p);

                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                stats.setKitPreference(KitType.CHANCE);
                stats.setActiveKit(KitType.CHANCE);

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
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

                opponent.getInventory().clear();
                opponent.getInventory().setHelmet(new ItemStack(Material.AIR));
                opponent.getInventory().setChestplate(new ItemStack(Material.AIR));
                opponent.getInventory().setLeggings(new ItemStack(Material.AIR));
                opponent.getInventory().setBoots(new ItemStack(Material.AIR));

                KitPvPStats killerStats = KitPvP.getInstance().getStats(opponent);
                killerStats.setActiveKit(KitType.DEFAULT);

                for (PotionEffect effect : opponent.getActivePotionEffects()) {
                    opponent.removePotionEffect(effect.getType());
                }
                opponent.setHealth(20D);

                String eliminated = Messages.BRACKETS_ELIMINATED.getMessage();
                eliminated = eliminated.replaceAll("%player%", p.getName())
                        .replaceAll("%remaining%", String.valueOf(getPlayers().size()));

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
                winner = winner.replaceAll("%player%", opponent.getName()).replaceAll("%event%", "Brackets");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
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
            if (isSpectating(p)) {
                removeSpectator(p);

                KitPvPStats stats = KitPvP.getInstance().getStats(p);
                stats.setKitPreference(KitType.CHANCE);
                stats.setActiveKit(KitType.CHANCE);

                Location spawn = KitPvP.getInstance().getSpawnLocation();
                p.teleport(new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ()));
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

                opponent.getInventory().clear();
                opponent.getInventory().setHelmet(new ItemStack(Material.AIR));
                opponent.getInventory().setChestplate(new ItemStack(Material.AIR));
                opponent.getInventory().setLeggings(new ItemStack(Material.AIR));
                opponent.getInventory().setBoots(new ItemStack(Material.AIR));

                KitPvPStats killerStats = KitPvP.getInstance().getStats(opponent);
                killerStats.setActiveKit(KitType.DEFAULT);

                for (PotionEffect effect : opponent.getActivePotionEffects()) {
                    opponent.removePotionEffect(effect.getType());
                }
                opponent.setHealth(20D);

                String eliminated = Messages.BRACKETS_ELIMINATED.getMessage();
                eliminated = eliminated.replaceAll("%player%", p.getName())
                                    .replaceAll("%remaining%", String.valueOf(getPlayers().size()));

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
                winner = winner.replaceAll("%player%", opponent.getName()).replaceAll("%event%", "Brackets");
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', winner));
                getEventManager().rewardPlayer(opponent);
                end();
            }
        }
    }

    public void startRound(Player fighter1, Player fighter2) {
        Location fighter1Loc = fighter1.getLocation();
        Location fighter2Loc = fighter2.getLocation();

        Location pos1 = getFighter1Position();
        Location pos2 = getFighter2Position();
        fighter1.teleport(new Location(pos1.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ(), fighter1Loc.getYaw(), fighter1Loc.getPitch()));
        fighter2.teleport(new Location(pos2.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ(), fighter2Loc.getYaw(), fighter2Loc.getPitch()));

        String roundstart = Messages.BRACKETS_ROUND_STARTED.getMessage();

        String fighters = Messages.BRACKETS_ROUND_FIGHTERS.getMessage();
        fighters = fighters.replaceAll("%fighter1%", fighter1.getName()).replaceAll("%fighter2%", fighter2.getName());

        applyChosenKit(fighter1);
        applyChosenKit(fighter2);

        sendMessageToSpectators(roundstart);
        sendMessageToSpectators(fighters);

        sendMessageToPlayers(roundstart);
        sendMessageToPlayers(fighters);
    }

    @EventHandler
    public void blockCommands(PlayerCommandPreprocessEvent event) {
        if (getEventManager().getCurrentEvent() == EventType.BRACKETS) {
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
    public void elimination(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getKiller() != null) {
            Player killer = player.getKiller();
            if (isFighting(player) && isFighting(killer)) {
                player.spigot().respawn();
                removePlayer(player);
                removeFighter(player);
                addSpectator(player);

                String deathmsg = Messages.BRACKETS_ELIMINATED.getMessage();
                deathmsg = deathmsg.replaceAll("%player%", player.getName())
                        .replaceAll("%killer%", killer.getName())
                        .replaceAll("%remaining%", String.valueOf(getPlayers().size()));

                sendMessageToPlayers(deathmsg);
                sendMessageToSpectators(deathmsg);

                removeFighter(killer);
                addParticipant(killer);
                Location loc = killer.getLocation();
                Location lobby = getLobbyLocation();

                killer.teleport(new Location(lobby.getWorld(), lobby.getX(), lobby.getY(), lobby.getZ(), loc.getYaw(), loc.getPitch()));

                killer.getInventory().clear();
                killer.getInventory().setHelmet(new ItemStack(Material.AIR));
                killer.getInventory().setChestplate(new ItemStack(Material.AIR));
                killer.getInventory().setLeggings(new ItemStack(Material.AIR));
                killer.getInventory().setBoots(new ItemStack(Material.AIR));

                KitPvPStats killerStats = KitPvP.getInstance().getStats(killer);
                killerStats.setActiveKit(KitType.DEFAULT);

                for (PotionEffect effect : killer.getActivePotionEffects()) {
                    killer.removePotionEffect(effect.getType());
                }
                killer.setHealth(20D);

                if (getPlayers().size() == 1) {
                    for (Player onlineP : Bukkit.getOnlinePlayers()) {
                        if (isPlaying(onlineP)) {
                            removePlayer(onlineP);
                            removeFighter(onlineP);
                            removeParticipant(onlineP);
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

                        stats.setKitPreference(KitType.CHANCE);
                        stats.setActiveKit(KitType.DEFAULT);
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
        if (getChosenKit() == KitType.KNIGHT) {
            stats.setActiveKit(KitType.KNIGHT);
            KitKnight kit = new KitKnight(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 8);
            return;
        }
        if (getChosenKit() == KitType.REAPER) {
            stats.setActiveKit(KitType.REAPER);
            KitReaper kit = new KitReaper(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 8);
            return;
        }
        if (getChosenKit() == KitType.PAINTBALL) {
            stats.setActiveKit(KitType.PAINTBALL);
            KitPaintball kit = new KitPaintball(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 8);
            return;
        }
        if (getChosenKit() == KitType.BUILDUHC) {
            stats.setActiveKit(KitType.BUILDUHC);
            KitBuildUHC kit = new KitBuildUHC(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 8);
            return;
        }
        if (getChosenKit() == KitType.DUBSTEP) {
            stats.setActiveKit(KitType.DUBSTEP);
            KitDubstep kit = new KitDubstep(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 8);
            return;
        }
        if (getChosenKit() == KitType.TELEPORTER) {
            stats.setActiveKit(KitType.TELEPORTER);
            KitTeleporter kit = new KitTeleporter(KitPvP.getInstance().getKitManager());
            kit.beginApplyKit(p);
            giveSoup(p, 8);
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
    public void hit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (getEventManager().getCurrentEvent() == EventType.BRACKETS) {
                    Player victim = (Player) event.getEntity();
                    Player attacker = (Player) event.getDamager();

                    if (isSpectating(victim) || isSpectating(attacker)) {
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
                if (isSpectating(victim) || isParticipating(victim)) {
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

    public void sendMessageToSpectators(String message) {
        if (this.spectators == null) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isSpectating(p)) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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


    public ArrayList<UUID> getSpectators() {
        return this.spectators;
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
