package net.skycade.kitpvp.events;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.scoreboard.ScoreboardHandler;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TagEvent extends RandomEvent implements Listener {

    private UUID infected = null;

    private Long begin = null;
    private List<UUID> inGame = null;
    private BukkitRunnable task;

    @Override
    public int getFrequencyPerDay() {
        return 12;
    }

    public TagEvent() {
        super();
        Bukkit.getServer().getPluginManager().registerEvents(this, KitPvP.getInstance());
    }

    @Override
    public void run() {
        List<Player> p = Bukkit.getOnlinePlayers()
                .stream().filter(e -> !KitPvP.getInstance().isInSpawnArea(e)).collect(Collectors.toList());

        if (p.isEmpty()) {
            super.end();
            return;
        }

        this.infected = p.get(ThreadLocalRandom.current().nextInt(p.size())).getUniqueId();
        begin = System.currentTimeMillis();

        Player infectedPlayer = Bukkit.getPlayer(this.infected);
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "INFECTION! " + ChatColor.GREEN + infectedPlayer.getName() + " is infected. Stay away from them for 5 minutes to get a coin bonus!");

        inGame = p.stream().map(Entity::getUniqueId).filter(e -> !e.equals(infected)).collect(Collectors.toList());

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boobs = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta meta = (LeatherArmorMeta) Bukkit.getItemFactory().getItemMeta(Material.LEATHER_HELMET);
        meta.setColor(Color.RED);

        helmet.setItemMeta(meta);
        chestplate.setItemMeta(meta);
        leggings.setItemMeta(meta);
        boobs.setItemMeta(meta);

        for (UUID uuid : inGame) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setHelmet(helmet.clone());
            inventory.setChestplate(chestplate.clone());
            inventory.setLeggings(leggings.clone());
            inventory.setBoots(boobs.clone());
        }

        meta.setColor(Color.GREEN);
        helmet.setItemMeta(meta);
        chestplate.setItemMeta(meta);
        leggings.setItemMeta(meta);
        boobs.setItemMeta(meta);

        PlayerInventory inventory = infectedPlayer.getInventory();
        inventory.clear();
        inventory.setHelmet(helmet.clone());
        inventory.setChestplate(chestplate.clone());
        inventory.setLeggings(leggings.clone());
        inventory.setBoots(boobs.clone());

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (inGame == null || begin == null) {
                    cancel();
                    return;
                }

                if (inGame.isEmpty() || System.currentTimeMillis() - begin > 5 * 60 * 1000L) {
                    end();
                    cancel();
                }
            }
        };
        task.runTaskTimer(KitPvP.getInstance(), 20L, 20L);

    }

    @Override
    public void end() {
        super.end();

        for (UUID uuid : inGame) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;
            KitPvPStats stats = KitPvP.getInstance().getStats(player);
            if (stats == null) continue;

            stats.setCoins(stats.getCoins() + 100);

            stats.getActiveKit().getKit().applyKit(player);
            stats.getActiveKit().getKit().giveSoup(player, 32);

            ScoreboardHandler.updatePlayer(player);
        }

        Player infectedPlayer = Bukkit.getPlayer(this.infected);

        begin = null;
        inGame = null;
        infected = null;

        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "INFECTION ENDED!");
        if (infectedPlayer == null) return;

        KitPvPStats stats = KitPvP.getInstance().getStats(infectedPlayer);

        stats.getActiveKit().getKit().applyKit(infectedPlayer);
        stats.getActiveKit().getKit().giveSoup(infectedPlayer, 32);

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (begin == null) return;
        inGame.remove(event.getEntity().getUniqueId());

        if (this.infected.equals(event.getEntity().getUniqueId())) {
            end();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (begin == null) return;
        inGame.remove(event.getPlayer().getUniqueId());

        if (this.infected.equals(event.getPlayer().getUniqueId())) {
            stop();
        }
    }

    public UUID getInfected() {
        return infected;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (begin == null) return;

        if (event.getEntity().getUniqueId().equals(this.infected)) {
            event.setCancelled(true);
            return;
        }

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damagee = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            if (damager.getUniqueId().equals(infected)) {
                boolean remove = inGame.remove(damagee.getUniqueId());
                if (!remove) return;

                KitPvPStats stats = KitPvP.getInstance().getStats(damagee);
                stats.getActiveKit().getKit().applyKit(damagee);
                stats.getActiveKit().getKit().giveSoup(damagee, 32);

                KitPvPStats damagerStats = KitPvP.getInstance().getStats(damager);
                damagerStats.setCoins(damagerStats.getCoins() + 15);

                ScoreboardHandler.updatePlayer(damager);

            } else if (inGame.contains(damagee.getUniqueId()) && inGame.contains(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    public void remove(UUID uuid) {
        inGame.remove(uuid);
    }

    public void stop() {
        begin = null;
        inGame = null;
        infected = null;
        if (task != null) task.cancel();
        super.end();

        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "INFECTION ENDED!");

    }
}
