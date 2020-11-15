package net.skycade.kitpvp.listeners.player;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.SkycadeCombat.data.CombatData;
import net.skycade.SkycadeCore.leveling.achievements.Achievement;
import net.skycade.SkycadeCore.leveling.achievements.CoreAchievementEvent;
import net.skycade.SkycadeCore.utility.TeleportUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPCoinsRewardEvent;
import net.skycade.kitpvp.bukkitevents.KitPvPKillPlayerEvent;
import net.skycade.kitpvp.bukkitevents.KitPvPKillstreakChange;
import net.skycade.kitpvp.coreclasses.member.Member;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.events.CaptureTheFlagEvent;
import net.skycade.kitpvp.events.DoubleCoinsEvent;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.kit.kits.disabled.KitMultishot;
import net.skycade.kitpvp.kit.kits.disabled.KitPyromancer;
import net.skycade.kitpvp.kit.kits.disabled.KitShroom;
import net.skycade.kitpvp.playerevents.EventType;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import net.skycade.kitpvp.ui.eventshopitems.EventShopManager;
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

import java.text.DecimalFormat;
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
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);

        if (event.getEntity() instanceof Player && PlayerMoveListener.getImmunePlayers().contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK && KitPvP.getInstance().isInSpawnArea((Player) event.getEntity()))
            event.setCancelled(true);

        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            event.setDamage((event.getDamage() * 0.25));
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

        // Grants assists and stuff to players using these entities
        if ((event.getDamager() instanceof TNTPrimed || event.getDamager() instanceof Fireball) && event.getDamager().getCustomName() != null && Bukkit.getOfflinePlayer(event.getDamager().getCustomName()).isOnline()) {
            if (CaptureTheFlagEvent.getInstance().getBegin() != null && CaptureTheFlagEvent.getInstance().isTeamRed(Bukkit.getPlayer(event.getDamager().getCustomName())) == CaptureTheFlagEvent.getInstance().isTeamRed(damagee)) {
                event.setCancelled(true);
                return;
            }
            addAssist(damagee, Bukkit.getPlayer(event.getDamager().getCustomName()), event.getDamage());
        }

        if (!(event.getDamager() instanceof Player))
            return;

        if (PlayerMoveListener.getImmunePlayers().contains(event.getDamager().getUniqueId()))
            PlayerMoveListener.removeImmunePlayer(event.getDamager().getUniqueId());

        if (plugin.getStats(damagee).getActiveKit().getKit().getKitType() == KitType.SONIC)
            ((KitSonic) plugin.getStats(damagee).getActiveKit().getKit()).disableSprint(damagee);

        Player damager = (Player) event.getDamager();

        plugin.getStats(damager).getActiveKit().getKit().onDamageDealHit(event, damager, damagee);
        plugin.getStats(damagee).getActiveKit().getKit().onDamageGetHit(event, damager, damagee);

        lastDamagerMap.put(damagee.getUniqueId(), event.getDamager());

        if (!damager.equals(damagee))
            addAssist(damagee, damager, event.getDamage());
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Golem)) return;
        if (event.getTarget() == null) return;
        if (event.getEntity().getName().contains(event.getTarget().getName()) || event.getTarget().getName().contains(event.getEntity().getName()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage("");
        if (KitPvP.getInstance().getEventManager().getCurrentEvent() == EventType.LMS) {
            if (KitPvP.getInstance().getEventManager().getLMS().isPlaying(event.getEntity())) {
                return;
            }
        }
        if (KitPvP.getInstance().getEventManager().getCurrentEvent() == EventType.BRACKETS) {
            if (KitPvP.getInstance().getEventManager().getBrackets().isPlaying(event.getEntity())) {
                return;
            }
        }

        Player diedPlayer = event.getEntity();

        // combat logging (remove combat log for the player who has died)
        CombatData.Combat combat = CombatData.getCombat(diedPlayer);
        combat.setInCombat(false);

        // special case for certain kits that allow the player to respawn at a custom point (like a block placed somewhere)
        boolean willRespawnAndReset = plugin.getStats(diedPlayer).getActiveKit().getKit().onDeath(diedPlayer, diedPlayer.getKiller());

        // removes teleporting entities so that they do not teleport to the player when they die (removes the wolves from the player before respawning, due to player teleporting back to wolves bug)
        Kit diedPlayerKit = plugin.getStats(MemberManager.getInstance().getMember(diedPlayer)).getActiveKit().getKit();
        diedPlayerKit.removeSummon(0, event.getEntity()); // remove the entities summoned
        diedPlayerKit.cancelRunnables(event.getEntity()); // cancel all the entity summoning events

        if (diedPlayer.isOnline() && willRespawnAndReset)
            respawn(event.getEntity()); // if the player who died is both online and does not have the special case flag set, then respawn

        Member diedMember = MemberManager.getInstance().getMember(diedPlayer); // get the member object for the player who has died
        KitPvPStats diedStats = plugin.getStats(diedMember); // get the stats object for the player who as died

        int diedStreak = diedStats.getStreak(); // the player who has died's kill streak
        diedStats.setDeaths(plugin.getStats(diedMember).getDeaths() + 1); // increase player who has died's death count

        boolean isKeepingKillStreak = EventShopManager.getInstance().isKeepingKs(diedPlayer);
        if (willRespawnAndReset && !isKeepingKillStreak) { // only reset kill streak of the dead player if they are: Going to reset AND do NOT have the event upgrade for keeping killstreak
            ScoreboardInfo.getInstance().updatePlayer(diedPlayer);
            diedStats.setStreak(0);
        }

        diedPlayer.getLocation().getWorld().playEffect(diedPlayer.getLocation(), Effect.SMOKE, 1); // play smoke particle effect at location they died

        ScoreboardInfo.getInstance().updatePlayer(diedPlayer); // update scoreboard for the player who died

        //Try to get the killer
        Player killerPlayer = diedPlayer.getKiller();

        if (killerPlayer == null && lastDamagerMap.get(diedPlayer.getUniqueId()) != null) {
            String customName;
            customName = lastDamagerMap.get(diedPlayer.getUniqueId()).getCustomName();
            if (customName == null)
                return;

            killerPlayer = Bukkit.getPlayer(customName);
            lastDamagerMap.remove(diedPlayer.getUniqueId());
        }
        if (killerPlayer == null || killerPlayer.equals(diedPlayer))
            return;

        // Getting the gang of the killer
        Gang gang = GangsPlusApi.getPlayersGang(killerPlayer);

        Member killerMember = MemberManager.getInstance().getMember(killerPlayer); // get member of the one who killed the player
        diedMember.setLastKiller(killerPlayer.getUniqueId()); // set the last killer of the player who died to the player who killed them

        killerPlayer.playSound(killerPlayer.getLocation(), "minecraft:entity.experience_orb.pickup", 1, 1); // play the "killed sound effect" to the player who killed the other

        // DEATH MESSAGE
        if (diedPlayer.isOnline()) // if the player who died is online
            KILLED_BY.msg(diedMember.getPlayer(), "%player%", killerMember.getName());

        //Update kills
        KitPvPStats killerStats = plugin.getStats(killerMember); // get the stats for the player who killed the other

        if (killerStats.getActiveKit() == KitType.ELITE) { // if the killer has kit elite active
            killerStats.getActiveKit().getKit().onDeath(diedPlayer, killerPlayer); // call the onDeath event
        }

        final int killerKills = killerStats.getKills() + 1; // get kills for the killer
        killerStats.setKills(killerKills); // add one to the kills ^

        //For missions
        KitPvPKillPlayerEvent killEvent = new KitPvPKillPlayerEvent(killerPlayer, killerStats.getActiveKit()); // get kill player event for the killer
        Bukkit.getServer().getPluginManager().callEvent(killEvent); // call the kill player event

        Bukkit.getServer().getPluginManager().callEvent(new CoreAchievementEvent(killerPlayer, "kitpvpkills") {
            @Override
            public boolean matcher(Achievement achievement) {
                return achievement.getJsonParams().get("kills").getAsInt() <= killerKills;
            }
        });

        double kdr = UtilMath.getKDR(killerKills, killerStats.getDeaths()); // get the KDR for the player who killed the other

        Bukkit.getServer().getPluginManager().callEvent(new CoreAchievementEvent(killerPlayer, "kitpvpkdr") { // call achievement event
            @Override
            public boolean matcher(Achievement achievement) {
                return achievement.getJsonParams().get("kdr").getAsDouble() <= kdr;
            }
        });

        //Update ks
        final int killerStreak = plugin.getStats(killerPlayer).getStreak() + 1; // get the killStreak for the player who killed the other
        killerStats.setStreak(killerStreak); // add one to their kill streak ^

        //For missions
        KitPvPKillstreakChange killstreakEvent = new KitPvPKillstreakChange(killerPlayer, killerStreak); // get the kill streak event for the killer
        Bukkit.getServer().getPluginManager().callEvent(killstreakEvent); // call the kill streak event

        if (killerStreak % 10 == 0) // if the kill streak is a multiple of 10
            HAS_KILLSTREAK.broadcast("%killer%", killerPlayer.getName(), "%ks%", Integer.toString(killerStreak)); // broadcast the kill streak message

        Bukkit.getServer().getPluginManager().callEvent(new CoreAchievementEvent(killerPlayer, "kitpvpstreak") { // call the kill streak achievement event
            @Override
            public boolean matcher(Achievement achievement) {
                return achievement.getJsonParams().get("streak").getAsInt() <= killerStreak;
            }
        });

        //The same player got killed multiple times
        if (noKillRewards(killerPlayer, diedPlayer)) { // check to see if the killer should get a reward for killing the player
            ScoreboardInfo.getInstance().updatePlayer(killerPlayer);
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
            COLLECTED_BOUNTY.msg(killerMember.getPlayer(), "%amount%", Integer.toString(bounty), "%player%", diedMember.getName());

        //Extra coins when a high ks gets broken
        int killstreakCoins = diedStreak >= 10 ? diedStreak : 0;
        if (diedStreak >= 10) {
            YOU_BROKE_KILLSTREAK.msg(killerMember.getPlayer(), "%amount%", Integer.toString(diedStreak), "%player%", diedMember.getName());
            BROKE_KILLSTREAK.broadcast("%killer%", killerPlayer.getName(), "%dead%", diedPlayer.getName(), "%ks%", Integer.toString(diedStreak));
        }

        int finalReward = 0;

        if (gang == null || !gang.getOnlineMembers().contains(diedPlayer)) {
            //Normal kill coins
            int base = KitPvP.getInstance().getConfig().getInt("kill-coins");
            int power = killerStats.getStreak() - 1 <= 150 ? killerStats.getStreak() - 1 : 150;
            int extra = (int) Math.ceil(base * Math.pow((100 + KitPvP.getInstance().getConfig().getInt("kill-bonus-percentage")) / ((double) 100), power)) + bounty;

            double modifier = KitPvP.getInstance().getConfig().getDouble("coins-modifier");

            finalReward = (int) Math.ceil(modifier / (double) 100 * (extra + killstreakCoins));

            if (DoubleCoinsEvent.isActive())
                finalReward = finalReward * 2;
        }

        // KILL MESSAGE
        DecimalFormat df = new DecimalFormat("###,###,###,###.##");
        if (killerPlayer.isOnline()) // if the player who killed is online
            YOU_KILLED.msg(killerMember.getPlayer(), "%player%", diedMember.getName(), "%coins%", df.format(finalReward));

        // This message is called if you have killed a member of your gang
        if (gang != null) {
            if (gang.getOnlineMembers().contains(diedPlayer))
                KILLED_GANG_MEMBER.msg(killerPlayer);
        }

        KitPvPCoinsRewardEvent coinsEvent = new KitPvPCoinsRewardEvent(killerPlayer, finalReward);
        Bukkit.getPluginManager().callEvent(coinsEvent);

        if (!coinsEvent.isCancelled()) {
            killerStats.giveCoins(coinsEvent.getNewCoins());
        }

        checkAssist(diedPlayer, killerPlayer);

        ScoreboardInfo.getInstance().updatePlayer(killerPlayer);
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

        if (proj.getType() == EntityType.SNOWBALL) {
            if (kit.getKitType() == KitType.SHACO)
                ((KitShaco) kit).onSnowballUse(shooter, event);
            else if (kit.getKitType() == KitType.FROSTY)
                ((KitFrosty) kit).onSnowballUse(shooter, event);
            else if (kit.getKitType() == KitType.SHROOM)
                ((KitShroom) kit).onSnowballUse(shooter, event);
            else if (kit.getKitType() == KitType.NECROMANCER)
                ((KitNecromancer) kit).onSnowballUse(shooter, event);
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
                NO_REWARDS.msg(killer, "%player%", killed.getName());
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
        Member member = MemberManager.getInstance().getMember(uuid, false);

        // Kills player if in combat and not in spawn
        CombatData.Combat combatData = CombatData.getCombat(event.getPlayer());

        if (member != null && !plugin.getSpawnRegion().contains(member.getPlayer())) {
            // Get the attacker
            // The notQuitter is the attacker
            UUID notQuitter = combatData.getLastDamager();
            // Increases kills for last damager to the player logging out

            if (notQuitter == null)
                return;

            Player attacker = Bukkit.getPlayer(notQuitter);

            if (attacker != null) {
                // Kills the logging out player
                event.getPlayer().damage(100, Bukkit.getPlayer(combatData.getLastDamager()));

                YOU_KILLED_LOGGED_OUT.msg(attacker, "%player%", member.getName());
                ScoreboardInfo.getInstance().updatePlayer(attacker);

                combatData.setLastDamager(null);
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
        p.teleport(TeleportUtil.getSpawn());
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), p::updateInventory, 10);
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> p.setVelocity(new org.bukkit.util.Vector(0, 0, 0)), 5);
    }
}
