package net.skycade.kitpvp.listeners.player;

import net.skycade.SkycadeCore.leveling.achievements.Achievement;
import net.skycade.SkycadeCore.leveling.achievements.CoreAchievementEvent;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPCoinsRewardEvent;
import net.skycade.kitpvp.bukkitevents.KitPvPKillPlayerEvent;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.DoubleCoinsEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.kit.kits.disabled.KitFireArcher;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.skycade.kitpvp.Messages.*;

public class PlayerDamageListener implements Listener {

    private final KitPvP plugin;
    private final Map<Entity, UUID> lastProjLaunch = new HashMap<>();
    private final Map<UUID, Entity> lastDamagerMap = new HashMap<>();
    private final Map<UUID, HashMap<UUID, Double>> killAssist = new HashMap<>();
    private final Map<UUID, HashMap<UUID, Integer>> samePlayerKill = new HashMap<>();

    public PlayerDamageListener(KitPvP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL)
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
            if (plugin.getSpawnRegion().contains(e.getEntity().getLocation())) {
                e.setCancelled(true);
                return;
            }
            if (!(e.getEntity() instanceof Player))
                return;
            Player damagee = (Player) e.getEntity();
            if (lastProjLaunch.containsKey(damagee)) {
                addAssist(damagee, Bukkit.getPlayer(lastProjLaunch.get(damagee)), e.getDamage());
                lastProjLaunch.remove(damagee);
                return;
            }
            if (!(e.getDamager() instanceof Player))
                return;
            if (plugin.getStats(damagee).getActiveKit().getKit().getKitType() == KitType.SONIC)
                ((KitSonic) plugin.getStats(damagee).getActiveKit().getKit()).disableSprint(damagee);

            Player damager = (Player) e.getDamager();

            plugin.getStats(damager).getActiveKit().getKit().onDamageDealHit(e, damager, damagee);
            plugin.getStats(damagee).getActiveKit().getKit().onDamageGetHit(e, damager, damagee);

            if (!damager.equals(damagee))
                addAssist(damagee, damager, e.getDamage());
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Golem)) return;
        if (event.getTarget() == null) return;
        if (event.getEntity().getName().equalsIgnoreCase(event.getTarget().getName() + " golem"))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.getDrops().clear();
        e.setDeathMessage("");

        //Removes the wolves from the player before respawning, due to player teleporting back to wolves bug
        Kit playerKit = plugin.getStats(MemberManager.getInstance().getMember(e.getEntity())).getActiveKit().getKit();
        playerKit.removeSummon(0, e.getEntity());
        playerKit.cancelRunnables(e.getEntity());

        if (e.getEntity().isOnline()) respawn(e.getEntity());

        Player died = e.getEntity();
        Member diedMem = MemberManager.getInstance().getMember(died);
        KitPvPStats diedStats = plugin.getStats(diedMem);

        // Increase highest killstreak
        int diedStreak = diedStats.getStreak();

        //Increase deaths and reset ks
        diedStats.setDeaths(plugin.getStats(diedMem).getDeaths() + 1);
        diedStats.setStreak(0);

        died.getLocation().getWorld().playEffect(died.getLocation(), Effect.SMOKE, 1);
        plugin.getKitManager().getSignMap().remove(died.getUniqueId());

        ScoreboardInfo.getInstance().updatePlayer(died);

        //Try to get the killer
        Player killer = died.getKiller();
        if (killer == null && lastDamagerMap.get(died.getUniqueId()) != null) {
            String customName;
            customName = lastDamagerMap.get(died.getUniqueId()).getCustomName();
            if (customName == null)
                return;

            killer = Bukkit.getPlayer(customName);
            lastDamagerMap.remove(died.getUniqueId());
        }
        if (killer == null || killer.equals(died))
            return;

        Member killerMem = MemberManager.getInstance().getMember(killer);
        diedMem.setLastKiller(killer.getUniqueId());

        killer.playSound(killer.getLocation(), "minecraft:entity.experience_orb.pickup", 1, 1);
        KILLED_BY.msg(diedMem.getPlayer(), "%player%", killerMem.getName());
        YOU_KILLED.msg(killerMem.getPlayer(), "%player%", diedMem.getName());

        //Update kills
        KitPvPStats stats = plugin.getStats(killerMem);

        //For missions
        KitPvPKillPlayerEvent killEvent = new KitPvPKillPlayerEvent(killer, stats.getActiveKit());
        Bukkit.getServer().getPluginManager().callEvent(killEvent);

        final int kills = stats.getKills() + 1;
        stats.setKills(kills);

        Bukkit.getServer().getPluginManager().callEvent(new CoreAchievementEvent(killer, "kitpvpkills") {
            @Override
            public boolean matcher(Achievement achievement) {
                return achievement.getJsonParams().get("kills").getAsInt() <= kills;
            }
        });

        double kdr = UtilMath.getKDR(kills, stats.getDeaths());

        Bukkit.getServer().getPluginManager().callEvent(new CoreAchievementEvent(killer, "kitpvpkdr") {
            @Override
            public boolean matcher(Achievement achievement) {
                return achievement.getJsonParams().get("kdr").getAsDouble() <= kdr;
            }
        });

        final int streak = plugin.getStats(killer).getStreak() + 1;
        stats.setStreak(streak);

        Bukkit.getServer().getPluginManager().callEvent(new CoreAchievementEvent(killer, "kitpvpstreak") {
            @Override
            public boolean matcher(Achievement achievement) {
                return achievement.getJsonParams().get("streak").getAsInt() <= streak;
            }
        });

        //The same player got killed multiple times
        if (noKillRewards(killer, died)) {
            ScoreboardInfo.getInstance().updatePlayer(killer);
            return;
        }

        //Give rewards to the killer
        //Bounties!
        int bounty = 0;

        int bountyLevel;
        for (bountyLevel = diedStreak; bountyLevel > 0; --bountyLevel) {
            bounty = KitPvP.getInstance().getConfig().getInt("bounties." + bountyLevel, 0);

            if (bounty != 0) break;
        }

        if (bounty != 0)
            COLLECTED_BOUNTY.msg(killerMem.getPlayer(), "%amount%", Integer.toString(bounty), "%player%", diedMem.getName());

        //Extra coins when a high ks gets broken
        int killstreakCoins = diedStats.getStreak() >= 10 ? diedStats.getStreak() : 0;
        if (diedStats.getStreak() >= 10)
            BROKE_KILLSTREAK.msg(killerMem.getPlayer(), "%amount%", Integer.toString(diedStats.getStreak()), "%player%", diedMem.getName());

        //Normal kill coins
        int base = KitPvP.getInstance().getConfig().getInt("kill-coins");
        int extra = (int) Math.ceil(base * Math.pow((100 + KitPvP.getInstance().getConfig().getInt("kill-bonus-percentage")) / ((double) 100), stats.getStreak() - 1)) + bounty;

        double modifier = KitPvP.getInstance().getConfig().getDouble("coins-modifier");
        int finalReward = (int) Math.ceil(modifier / (double) 100 * (extra + killstreakCoins));

        if (DoubleCoinsEvent.isActive())
            finalReward = finalReward * 2;

        KitPvPCoinsRewardEvent event = new KitPvPCoinsRewardEvent(killer, finalReward);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            stats.setCoins(stats.getCoins() + event.getNewCoins());
        }

        checkAssist(died, killer);

        ScoreboardInfo.getInstance().updatePlayer(killer);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile proj = e.getEntity();
        if (!(proj.getShooter() instanceof Player))
            return;
        Player shooter = (Player) proj.getShooter();
        if (plugin.isInSpawnArea(shooter)) {
            e.setCancelled(true);
            return;
        }
        lastProjLaunch.put(proj, shooter.getUniqueId());

        Kit kit = plugin.getStats(shooter).getActiveKit().getKit();

        if (proj.getType() == EntityType.FISHING_HOOK) {
            if (kit.getKitType() == KitType.FISHERMAN)
                ((KitFisherman) kit).onRodUse(shooter, e);
        } else if (proj.getType() == EntityType.SNOWBALL) {
            if (kit.getKitType() == KitType.SHACO)
                ((KitShaco) kit).onSnowballUse(e);
            else if (kit.getKitType() == KitType.FROSTY)
                ((KitFrosty) kit).onSnowballUse(e);
            else if (kit.getKitType() == KitType.SHROOM)
                ((KitShroom) kit).onSnowballUse(e);
        } else if (proj.getType() == EntityType.ARROW) {
            if (kit.getKitType() == KitType.ARCHER)
                ((KitArcher) kit).onArrowLaunch(shooter, e);
            else if (kit.getKitType() == KitType.FIREARCHER)
                ((KitFireArcher) kit).onArrowLaunch(shooter, e);
            else if (kit.getKitType() == KitType.SNIPER)
                ((KitSniper) kit).onArrowLaunch(shooter, e);
            else if (kit.getKitType() == KitType.MULTISHOT)
                ((KitMultishot) kit).onArrowLaunch(shooter, e);
            else if (kit.getKitType() == KitType.PYROMANCER)
                ((KitPyromancer) kit).onArrowLaunch(shooter, e);
        }
    }

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        Player damagee = (Player) e.getEntity();
        Entity damager = e.getDamager();

        if (damager.getCustomName() != null)
            lastDamagerMap.put(damagee.getUniqueId(), e.getDamager());
        if (!(e.getDamager() instanceof Projectile))
            return;
        Projectile proj = (Projectile) damager;
        if (!(proj.getShooter() instanceof Player))
            return;
        Player shooter = (Player) proj.getShooter();
        if (plugin.isInSpawnArea(shooter) || plugin.isInSpawnArea(damagee))
            return;

        Kit damageeKit = plugin.getStats(damagee).getActiveKit().getKit();
        if (damageeKit.getKitType() == KitType.ENDERMAN) {
            ((KitEnderman) damageeKit).onArrowHit(shooter, damagee, e);
        }

        Kit kit = plugin.getStats(shooter).getActiveKit().getKit();
        if (kit.getKitType() == KitType.SHACO)
            ((KitShaco) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.FROSTY)
            ((KitFrosty) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.SHROOM)
            ((KitShroom) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.ARCHER)
            ((KitArcher) kit).onArrowHit(shooter, damagee, e);
        else if (kit.getKitType() == KitType.FIREARCHER)
            ((KitFireArcher) kit).onArrowHit(shooter, damagee, e);
        else if (kit.getKitType() == KitType.SNIPER)
            ((KitSniper) kit).onArrowHit(shooter, damagee, e);

        if (!damager.equals(damagee))
            addAssist(damagee, shooter, e.getDamage());
    }

    private void addAssist(Player damaged, Player damager, double damage) {
        if (plugin.getSpawnRegion().contains(damager)) {
            return;
        }
        if (!killAssist.containsKey(damaged.getUniqueId())) {
            HashMap<UUID, Double> inner = new HashMap<>();
            inner.put(damager.getUniqueId(), damage);
            killAssist.put(damaged.getUniqueId(), inner);
            return;
        }
        if (!(killAssist.get(damaged.getUniqueId()).containsKey(damager.getUniqueId()))) {
            killAssist.get(damaged.getUniqueId()).put(damager.getUniqueId(), damage);
            return;
        }
        final HashMap<UUID, Double> inner = killAssist.get(damaged.getUniqueId());
        for (Map.Entry<UUID, Double> entry : inner.entrySet()) {
            double value = entry.getValue();
            value += damage;
            killAssist.get(damaged.getUniqueId()).put(entry.getKey(), value);
        }
    }

    private boolean noKillRewards(Player killer, Player killed) {
        HashMap<UUID, Integer> playerKilledMap = samePlayerKill.containsKey(killer.getUniqueId())
                ? samePlayerKill.get(killer.getUniqueId()) : new HashMap<>();
        if (!(samePlayerKill.containsKey(killer.getUniqueId()))) {
            playerKilledMap.put(killed.getUniqueId(), 1);
            samePlayerKill.put(killer.getUniqueId(), playerKilledMap);
            return false;
        }

        if (playerKilledMap.containsKey(killed.getUniqueId())) {
            int amount = playerKilledMap.get(killed.getUniqueId());
            amount++;
            playerKilledMap.put(killed.getUniqueId(), amount);
            samePlayerKill.put(killer.getUniqueId(), playerKilledMap);
            if (amount > 3) {
                NO_REWARDS.msg(killer);
                return true;
            }
        } else {
            playerKilledMap.clear();
            playerKilledMap.put(killed.getUniqueId(), 1);
            samePlayerKill.put(killer.getUniqueId(), playerKilledMap);
        }
        return false;

    }

    private void checkAssist(Player killed, Player killer) {
        if (killAssist.containsKey(killed.getUniqueId())) {
            HashMap<UUID, Double> inner = killAssist.get(killed.getUniqueId());
            double total = 0.0;

            for (Map.Entry<UUID, Double> entry : inner.entrySet())
                total += entry.getValue();

            for (Map.Entry<UUID, Double> entry : inner.entrySet()) {
                Player keyPlayer = Bukkit.getPlayer(entry.getKey());
                if (keyPlayer != null) {
                    int coins = KitPvP.getInstance().getConfig().getInt("kill-coins");
                    double percentage = ((entry.getValue() * 100) / total) / 100;

                    if (keyPlayer != killed && keyPlayer != killer) {
                        int assist = (int) (coins * percentage);
                        if (assist > 0) {
                            ASSIST_REWARD.msg(keyPlayer, "%amount%", Integer.toString(assist), "%player%", killed.getName());
                        }

                        plugin.getStats(keyPlayer).setCoins(plugin.getStats(keyPlayer).getCoins() + assist);
                        plugin.getStats(keyPlayer).setAssists(plugin.getStats(keyPlayer).getAssists() + 1);
                    }
                }
            }
            killAssist.remove(killed.getUniqueId());
        }
        ScoreboardInfo.getInstance().updatePlayer(killer);
        ScoreboardInfo.getInstance().updatePlayer(killed);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Member member = MemberManager.getInstance().getMember(e.getPlayer().getUniqueId(), false);
        if (member != null) {
            member.setLastKiller(null);
        }
        lastDamagerMap.remove(uuid);
        killAssist.remove(uuid);
        samePlayerKill.remove(uuid);
    }

    private void respawn(Player p) {
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> UtilPlayer.reset(p), 1);
        p.setHealth(p.getMaxHealth());
        p.setVelocity(new Vector(0, 0, 0));
        p.setGameMode(GameMode.SURVIVAL);
        p.teleport(KitPvP.getInstance().getSpawnLocation());
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), p::updateInventory, 10);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> p.setVelocity(new org.bukkit.util.Vector(0, 0, 0)), 5);
        KitPvPStats stats = KitPvP.getInstance().getStats(p);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            stats.getActiveKit().getKit().giveSoup(p, 32);
        }, 5);
        stats.applyKitPreference();
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            stats.getActiveKit().getKit().beginApplyKit(p);
            KitPvP.getInstance().getEventShopManager().reapplyUpgrades(p);
        }, 3);
    }
}
