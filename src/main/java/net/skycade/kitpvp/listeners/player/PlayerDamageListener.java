package net.skycade.kitpvp.listeners.player;

import net.minelink.ctplus.CombatTagPlus;
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
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
            if (plugin.getSpawnRegion().contains(event.getEntity().getLocation())) {
                event.setCancelled(true);
                return;
            }
            if (!(event.getEntity() instanceof Player))
                return;
            Player damagee = (Player) event.getEntity();
            if (lastProjLaunch.containsKey(damagee)) {
                addAssist(damagee, Bukkit.getPlayer(lastProjLaunch.get(damagee)), event.getDamage());
                lastProjLaunch.remove(damagee);
                return;
            }
            if (!(event.getDamager() instanceof Player))
                return;
            if (plugin.getStats(damagee).getActiveKit().getKit().getKitType() == KitType.SONIC)
                ((KitSonic) plugin.getStats(damagee).getActiveKit().getKit()).disableSprint(damagee);

            Player damager = (Player) event.getDamager();

            plugin.getStats(damager).getActiveKit().getKit().onDamageDealHit(event, damager, damagee);
            plugin.getStats(damagee).getActiveKit().getKit().onDamageGetHit(event, damager, damagee);

            if (!damager.equals(damagee))
                addAssist(damagee, damager, event.getDamage());
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Golem)) return;
        if (event.getTarget() == null) return;
        if (event.getEntity().getName().equalsIgnoreCase(event.getTarget().getName() + " golem"))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage("");

        boolean resetStats = plugin.getStats(event.getEntity()).getActiveKit().getKit().onDeath(event.getEntity());

        //Removes the wolves from the player before respawning, due to player teleporting back to wolves bug
        Kit playerKit = plugin.getStats(MemberManager.getInstance().getMember(event.getEntity())).getActiveKit().getKit();
        playerKit.removeSummon(0, event.getEntity());
        playerKit.cancelRunnables(event.getEntity());

        if (event.getEntity().isOnline() && resetStats) respawn(event.getEntity());

        Player died = event.getEntity();
        Member diedMem = MemberManager.getInstance().getMember(died);
        KitPvPStats diedStats = plugin.getStats(diedMem);

        // Increase highest killstreak
        int diedStreak = diedStats.getStreak();

        //Increase deaths
        diedStats.setDeaths(plugin.getStats(diedMem).getDeaths() + 1);

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

        final int kills = stats.getKills() + 1;
        stats.setKills(kills);

        if (resetStats)
            diedStats.setStreak(0);

        //For missions
        KitPvPKillPlayerEvent killEvent = new KitPvPKillPlayerEvent(killer, stats.getActiveKit());
        Bukkit.getServer().getPluginManager().callEvent(killEvent);

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

        //Update ks
        final int streak = plugin.getStats(killer).getStreak() + 1;
        stats.setStreak(streak);

        if (streak % 10 == 0)
            HAS_KILLSTREAK.broadcast("%killer%", killer.getName(), "%ks%", Integer.toString(streak));

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
        int killstreakCoins = diedStreak >= 10 ? diedStreak : 0;
        if (diedStreak >= 10) {
            YOU_BROKE_KILLSTREAK.msg(killerMem.getPlayer(), "%amount%", Integer.toString(diedStreak), "%player%", diedMem.getName());
            BROKE_KILLSTREAK.broadcast("%killer%", killer.getName(), "%dead%", died.getName(), "%ks%", Integer.toString(diedStreak));
        }

        //Normal kill coins
        int base = KitPvP.getInstance().getConfig().getInt("kill-coins");
        int extra = (int) Math.ceil(base * Math.pow((100 + KitPvP.getInstance().getConfig().getInt("kill-bonus-percentage")) / ((double) 100), stats.getStreak() - 1)) + bounty;

        double modifier = KitPvP.getInstance().getConfig().getDouble("coins-modifier");
        int finalReward = (int) Math.ceil(modifier / (double) 100 * (extra + killstreakCoins));

        if (DoubleCoinsEvent.isActive())
            finalReward = finalReward * 2;

        KitPvPCoinsRewardEvent coinsEvent = new KitPvPCoinsRewardEvent(killer, finalReward);
        Bukkit.getPluginManager().callEvent(coinsEvent);

        if (!coinsEvent.isCancelled()) {
            stats.giveCoins(coinsEvent.getNewCoins());
        }

        checkAssist(died, killer);

        ScoreboardInfo.getInstance().updatePlayer(killer);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        if (!(proj.getShooter() instanceof Player))
            return;
        Player shooter = (Player) proj.getShooter();

        if (plugin.isInSpawnArea(shooter)) {
            event.setCancelled(true);
            return;
        }

        lastProjLaunch.put(proj, shooter.getUniqueId());

        Kit kit = plugin.getStats(shooter).getActiveKit().getKit();

        if (proj.getType() == EntityType.FISHING_HOOK) {
            if (kit.getKitType() == KitType.FISHERMAN)
                ((KitFisherman) kit).onRodUse(shooter, event);
        } else if (proj.getType() == EntityType.SNOWBALL) {
            if (kit.getKitType() == KitType.SHACO)
                ((KitShaco) kit).onSnowballUse(shooter, event);
            else if (kit.getKitType() == KitType.FROSTY)
                ((KitFrosty) kit).onSnowballUse(shooter,event);
            else if (kit.getKitType() == KitType.SHROOM)
                ((KitShroom) kit).onSnowballUse(shooter,event);
            else if (kit.getKitType() == KitType.NECROMANCER)
                ((KitNecromancer) kit).onSnowballUse(shooter,event);
        } else if (proj.getType() == EntityType.ARROW) {
            if (kit.getKitType() == KitType.ARCHER)
                ((KitArcher) kit).onArrowLaunch(shooter, event);
            else if (kit.getKitType() == KitType.FIREARCHER)
                ((KitFireArcher) kit).onArrowLaunch(shooter, event);
            else if (kit.getKitType() == KitType.SNIPER)
                ((KitSniper) kit).onArrowLaunch(shooter, event);
            else if (kit.getKitType() == KitType.MULTISHOT)
                ((KitMultishot) kit).onArrowLaunch(shooter, event);
            else if (kit.getKitType() == KitType.PYROMANCER)
                ((KitPyromancer) kit).onArrowLaunch(shooter, event);
            else if (kit.getKitType() == KitType.BUILDUHC)
                ((KitBuildUHC) kit).onArrowLaunch(shooter, event);
        } else if (proj.getType() == EntityType.ENDER_PEARL) {
            if (kit.getKitType() == KitType.TELEPORTER)
                ((KitTeleporter) kit).onPearlLaunch(shooter, event);
        }
    }

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player damagee = (Player) event.getEntity();
        Entity damager = event.getDamager();

        if (damager.getCustomName() != null)
            lastDamagerMap.put(damagee.getUniqueId(), event.getDamager());
        if (!(event.getDamager() instanceof Projectile))
            return;
        Projectile proj = (Projectile) damager;
        if (!(proj.getShooter() instanceof Player))
            return;
        Player shooter = (Player) proj.getShooter();
        if (plugin.isInSpawnArea(shooter) || plugin.isInSpawnArea(damagee))
            return;

        Kit damageeKit = plugin.getStats(damagee).getActiveKit().getKit();
        if (damageeKit.getKitType() == KitType.ENDERMAN) {
            ((KitEnderman) damageeKit).onArrowHit(shooter, damagee, event);
            return;
        }

        Kit kit = plugin.getStats(shooter).getActiveKit().getKit();
        if (kit.getKitType() == KitType.SHACO)
            ((KitShaco) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.FROSTY)
            ((KitFrosty) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.SHROOM)
            ((KitShroom) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.NECROMANCER)
            ((KitNecromancer) kit).onSnowballHit(shooter, damagee);
        else if (kit.getKitType() == KitType.ARCHER)
            ((KitArcher) kit).onArrowHit(shooter, damagee, event);
        else if (kit.getKitType() == KitType.FIREARCHER)
            ((KitFireArcher) kit).onArrowHit(shooter, damagee, event);
        else if (kit.getKitType() == KitType.SNIPER)
            ((KitSniper) kit).onArrowHit(shooter, damagee, event);

        if (!damager.equals(damagee))
            addAssist(damagee, shooter, event.getDamage());
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

                        plugin.getStats(keyPlayer).giveCoins(assist);
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
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Member member = MemberManager.getInstance().getMember(event.getPlayer().getUniqueId(), false);

        // Kills player if in combat and not in spawn
        CombatTagPlus pl = (CombatTagPlus) Bukkit.getPluginManager().getPlugin("CombatTagPlus");
        if (member != null && !plugin.getSpawnRegion().contains(member.getPlayer()) && pl.getTagManager().isTagged(event.getPlayer().getUniqueId())) {
            plugin.getStats(member).setDeaths(plugin.getStats(member.getPlayer()).getDeaths() + 1);

            // Increases kills for last damager to the player logging out
            Player attacker = Bukkit.getPlayer(lastDamagerMap.get(uuid).getCustomName());

            if (attacker != null) {
                Member lastDamager = MemberManager.getInstance().getMember(attacker.getUniqueId(), false);
                plugin.getStats(lastDamager).setKills(plugin.getStats(lastDamager).getKills() + 1);
                YOU_KILLED_LOGGED_OUT.msg(lastDamager.getPlayer());
            }

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
