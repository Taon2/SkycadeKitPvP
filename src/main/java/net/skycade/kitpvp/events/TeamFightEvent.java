package net.skycade.kitpvp.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.connorlinfoot.actionbarapi.ActionBarAPI;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.SkycadeCore.utility.scoreboard.ScoreboardManager;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPCoinsRewardEvent;
import net.skycade.kitpvp.bukkitevents.KitPvPEventStartEvent;
import net.skycade.kitpvp.events.teamfight.TeamFightPacketListener;
import net.skycade.kitpvp.events.teamfight.TeamFightStartDelay;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static net.skycade.kitpvp.Messages.*;

public class TeamFightEvent extends RandomEvent implements Listener {

    private BukkitRunnable task;

    private int prizeAmount = 5;
    private int participationAmount = 1;

    private static TeamFightEvent instance;

    private List<UUID> lastWinners = new ArrayList<>();
    private Long lastEvent = -1L;
    private BukkitRunnable actionBarTask;
    private ScoreboardManager.QueuedDisplay queuedDisplay;

    public TeamFightEvent() {
        super();
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, KitPvP.getInstance());

        ProtocolLibrary.getProtocolManager().addPacketListener(new TeamFightPacketListener());
    }

    private Set<UUID> team1 = new HashSet<>();
    private Set<UUID> team2 = new HashSet<>();

    private int team1Kills = 0;
    private int team2Kills = 0;

    private Long begin = null;

    private static final ItemStack BANNER1;
    private static final ItemStack BANNER2;

    static {
        BannerMeta meta;

        BANNER1 = new ItemStack(Material.BANNER, 1, (short) 0);
        if (!BANNER1.hasItemMeta())
            meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);
        else
            meta = (BannerMeta) BANNER1.getItemMeta();

        meta.addPattern(new Pattern(DyeColor.RED, PatternType.BASE));
        BANNER1.setItemMeta(meta);

        BANNER2 = new ItemStack(Material.BANNER, 1, (short) 0);
        if (!BANNER2.hasItemMeta())
            meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BANNER);
        else
            meta = (BannerMeta) BANNER2.getItemMeta();

        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.BASE));
        BANNER2.setItemMeta(meta);
    }

    public static ItemStack getBannerFor(UUID uuid) {
        RandomEvent current = RandomEvent.getCurrent();
        if (!(current instanceof TeamFightEvent)) return null;

        if (((TeamFightEvent) current).team1.contains(uuid)) {
            return BANNER1.clone();
        } else if (((TeamFightEvent) current).team2.contains(uuid)) {
            return BANNER2.clone();
        }

        return null;
    }

    @Override
    public int getFrequencyPerDay() {
        return 3;
    }

    @Override
    public void run() {

        begin = System.currentTimeMillis() + 60 * 1000L;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (rand.nextInt(2) == 0) {
                team1.add(player.getUniqueId());
            } else {
                team2.add(player.getUniqueId());
            }
        }

        if (team1.size() > team2.size()) {
            int diff = team1.size() - team2.size();
            if (diff != 1) {
                for (int x = 0; x < diff / 2; ++x) {
                    UUID uuid = team1.stream().findAny().orElse(null);
                    team1.remove(uuid);
                    team2.add(uuid);
                }
            }
        } else if (team2.size() > team1.size()) {
            int diff = team2.size() - team1.size();
            if (diff != 1) {
                for (int x = 0; x < diff / 2; ++x) {
                    UUID uuid = team2.stream().findAny().orElse(null);
                    team2.remove(uuid);
                    team1.add(uuid);
                }
            }
        }

        new TeamFightStartDelay(begin, 60);
    }

    public void start() {
        lastWinners.clear();

        TEAMFIGHT_START.broadcast();

        Bukkit.getOnlinePlayers().forEach(player -> {
            KitPvPEventStartEvent eventStartEvent = new KitPvPEventStartEvent(player);
            Bukkit.getServer().getPluginManager().callEvent(eventStartEvent);

            if (team1.contains(player.getUniqueId())) {
                TEAMFIGHT_TEAM1.msg(player);
            } else {
                TEAMFIGHT_TEAM2.msg(player);
            }
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                refreshArmor();
            }
        }.runTask(KitPvP.getInstance());

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (begin == null) {
                    cancel();
                    return;
                }
                if (System.currentTimeMillis() - begin > 5 * 60 * 1000L) {
                    end();
                    cancel();
                }
            }
        };
        task.runTaskTimer(KitPvP.getInstance(), 20L, 20L);

        actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (begin == null || begin > System.currentTimeMillis()) return;
                int sec = ((Long) (5 * 60 - (System.currentTimeMillis() - begin) / 1000L)).intValue();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    boolean red = team1.contains(player.getUniqueId());
                    ActionBarAPI.sendActionBar(player,
                            ChatColor.GREEN +
                                    "You are in team " + (red ? ChatColor.RED + "" + ChatColor.BOLD + "RED" : ChatColor.BLUE + "" + ChatColor.BOLD + "BLUE") + ChatColor.GREEN + "!" + ChatColor.WHITE + " - " +
                    ChatColor.GOLD + CoreUtil.niceFormat(sec, true));
                }

                ScoreboardManager.getInstance().updateScores("timeleft", p -> ChatColor.GRAY + "Time left: " + ChatColor.YELLOW + CoreUtil.niceFormat(sec, true));
                ScoreboardManager.getInstance().updateScores("team1", p -> ChatColor.RED + "" + ChatColor.BOLD + "RED" + ": " + ChatColor.GOLD + team1Kills);
                ScoreboardManager.getInstance().updateScores("team2", p -> ChatColor.BLUE + "" + ChatColor.BOLD + "BLUE" + ": " + ChatColor.GOLD + team2Kills);
            }
        };
        actionBarTask.runTaskTimer(KitPvP.getInstance(), 20L, 20L);

        queuedDisplay = ScoreboardManager.getInstance().addQueuedDisplay((p, d) -> {
            int i = 10;
            int sec = ((Long) (5 * 60 - (System.currentTimeMillis() - begin) / 1000L)).intValue();
            d.setTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Team Fight");
            d.setScore("blank" + i, " ", --i);
            d.setScore("your", ChatColor.GRAY + "Your team: " + (team2.contains(p.getUniqueId()) ?
                    ChatColor.BLUE + "" + ChatColor.BOLD + "BLUE" :
                    ChatColor.RED + "" + ChatColor.BOLD + "RED"
            ), --i);
            d.setScore("blank" + i, "  ", --i);
            d.setScore("timeleft", ChatColor.GRAY + "Time left: " + ChatColor.YELLOW + CoreUtil.niceFormat(sec, true), --i);
            d.setScore("blank" + i, "   ", --i);
            d.setScore("kills", ChatColor.GRAY + "Kills", --i);
            d.setScore("team1", ChatColor.RED + "" + ChatColor.BOLD + "RED" + ": " + ChatColor.GOLD + team1Kills, --i);
            d.setScore("team2", ChatColor.BLUE + "" + ChatColor.BOLD + "BLUE" + ": " + ChatColor.GOLD + team2Kills, --i);
        }, 2);
    }

    private void refreshArmor() {
        Bukkit.getOnlinePlayers().forEach(this::refreshArmor);
    }

    private void refreshArmor(Player p) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        ItemStack helmet = p.getInventory().getHelmet();
        packet.getIntegers().write(0, p.getEntityId());
        packet.getIntegers().write(1, 4);
        if (begin == null || begin > System.currentTimeMillis()) {
            packet.getItemModifier().write(0, helmet == null ? new ItemStack(Material.AIR) : helmet);
        } else {
            if (team1.contains(p.getUniqueId())) {
                packet.getItemModifier().write(0, BANNER1);
            } else {
                packet.getItemModifier().write(0, BANNER2);
            }
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.getUniqueId().equals(p.getUniqueId())) continue;
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet, false);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (begin == null) return;

        UUID uuid = event.getPlayer().getUniqueId();

        if (!team1.contains(uuid) && !team2.contains(uuid)) {

            if (team1Kills > team2Kills) {
                // should assign to 2, but
                if (getOnlineSize(team2) - getOnlineSize(team1) > 4) {
                    // assign to 1
                    team1.add(uuid);
                } else {
                    team2.add(uuid);
                }
            } else if (team1Kills == team2Kills) {
                int diff = getOnlineSize(team1) - getOnlineSize(team2);
                if (diff == 0) {
                    if (ThreadLocalRandom.current().nextInt(2) == 0) {
                        team1.add(uuid);
                    } else {
                        team2.add(uuid);
                    }
                } else if (diff > 0) {
                    team2.add(uuid);
                } else {
                    team1.add(uuid);
                }
            } else {
                // should assign to 1, but
                if (getOnlineSize(team1) - getOnlineSize(team2) > 4) {
                    // assign to 2
                    team2.add(uuid);
                } else {
                    team1.add(uuid);
                }
            }
        }

        if (begin <= System.currentTimeMillis()) {
            refreshArmor(event.getPlayer());
            if (team1.contains(uuid)) {
                TEAMFIGHT_TEAM1.msg(event.getPlayer());
            } else {
                TEAMFIGHT_TEAM2.msg(event.getPlayer());
            }
        }
    }

    @Override
    public void end() {
        super.end();

        begin = null;
        new BukkitRunnable() {
            @Override
            public void run() {
                refreshArmor();
            }
        }.runTask(KitPvP.getInstance());

        TEAMFIGHT_FINAL_STATS.broadcast(
                "%kills1%", team1Kills + "",
                "%s1%", team1Kills != 1 ? "s" : "",
                "%s2%", team2Kills != 1 ? "s" : "",
                "%kills2%", team2Kills + ""
        );

        lastWinners.clear();
        lastEvent = System.currentTimeMillis();
        if (team1Kills == team2Kills) {
            TEAMFIGHT_DRAW.broadcast();
        } else if (team1Kills > team2Kills) {
            TEAMFIGHT_WINNER.broadcast("%team%", ChatColor.translateAlternateColorCodes('&', "&c&lRED"));
            team1.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> {
                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                if (stats != null) {
                    stats.setEventCoins(stats.getEventTokens() + prizeAmount);
                    TEAMFIGHT_WON.msg(player, "%amount%", Integer.toString(prizeAmount));
                }
            });
            team2.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> {
                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                if (stats != null) {
                    stats.setEventCoins(stats.getEventTokens() + participationAmount);
                    TEAMFIGHT_PARTICIPATE.msg(player, "%amount%", Integer.toString(participationAmount));
                }
            });
            lastWinners.addAll(team1);
        } else {
            TEAMFIGHT_WINNER.broadcast("%team%", ChatColor.translateAlternateColorCodes('&', "&9&lBLUE"));
            team2.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> {
                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                if (stats != null) {
                    stats.setEventCoins(stats.getEventTokens() + prizeAmount);
                    TEAMFIGHT_WON.msg(player, "%amount%", Integer.toString(prizeAmount));
                }
            });
            team1.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> {
                KitPvPStats stats = KitPvP.getInstance().getStats(player);
                if (stats != null) {
                    stats.setEventCoins(stats.getEventTokens() + participationAmount);
                    TEAMFIGHT_PARTICIPATE.msg(player, "%amount%", Integer.toString(participationAmount));
                }
            });
            lastWinners.addAll(team2);
        }

        team1Kills = 0;
        team2Kills = 0;
        team1.clear();
        team2.clear();
        begin = null;

        if (task != null) task.cancel();
        if (actionBarTask != null) actionBarTask.cancel();
        if (queuedDisplay != null) queuedDisplay.remove();

        refreshArmor();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onKitPvPCoinsReward(KitPvPCoinsRewardEvent event) {
        if (lastEvent < System.currentTimeMillis() - 30 * 60 * 1000L) return;
        if (lastWinners.contains(event.getPlayer().getUniqueId())) {
            event.setNewCoins(event.getCoins() * 2);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (begin == null || begin > System.currentTimeMillis()) return;
        UUID uuid = event.getEntity().getUniqueId();
        if (team1.contains(uuid)) {
            ++team2Kills;
        } else {
            ++team1Kills;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (begin == null || begin > System.currentTimeMillis()) return;

        if (!(event.getEntity() instanceof Player)) return;
        Player damager = null;
        if (event.getDamager() instanceof Projectile) {
            if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) return;

            damager = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if (damager == null) {
            if (event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }
            else return;
        }
        Player damagee = (Player) event.getEntity();

        if (team1.contains(damager.getUniqueId()) && team1.contains(damagee.getUniqueId())) {
            event.setCancelled(true);
        } else if (team2.contains(damager.getUniqueId()) && team2.contains(damagee.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private int getOnlineSize(Collection<UUID> c) {
        return c.stream().mapToInt(e -> Bukkit.getPlayer(e) != null ? 1 : 0).sum();
    }

    public static TeamFightEvent getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "teamfight";
    }

    public Long getBegin() {
        return begin;
    }
}
