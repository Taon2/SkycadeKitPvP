package net.skycade.kitpvp.events;

import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPEventStartEvent;
import net.skycade.kitpvp.scoreboard.ScoreboardInfo;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static net.skycade.kitpvp.Messages.TAG_ENDED;
import static net.skycade.kitpvp.Messages.TAG_START;

public class TagEvent extends RandomEvent implements Listener {

    private UUID infected = null;

    private static TagEvent instance;
    private Long begin = null;
    private List<UUID> inGame = null;
    private BukkitRunnable task;

    @Override
    public int getFrequencyPerDay() {
        return 0;
    }

    public TagEvent() {
        super();
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, KitPvP.getInstance());
    }

    @Override
    public void run() {
        List<Player> p = Bukkit.getOnlinePlayers()
                .stream().filter(e ->
                        !KitPvP.getInstance().isInSpawnArea(e)
                                && !VanishStatus.isVanished(e.getUniqueId())
                ).collect(Collectors.toList());

        if (p.isEmpty()) {
            super.end();
            return;
        }

        this.infected = p.get(ThreadLocalRandom.current().nextInt(p.size())).getUniqueId();
        begin = System.currentTimeMillis();

        Player infectedPlayer = Bukkit.getPlayer(this.infected);

        KitPvPEventStartEvent eventStartEvent = new KitPvPEventStartEvent(infectedPlayer);
        Bukkit.getServer().getPluginManager().callEvent(eventStartEvent);

        TAG_START.broadcast("%player%", infectedPlayer.getName());
        for(Player pl: Bukkit.getOnlinePlayers()){
            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
        }

        inGame = p.stream().map(Entity::getUniqueId).filter(e -> !e.equals(infected)).collect(Collectors.toList());

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta meta = (LeatherArmorMeta) Bukkit.getItemFactory().getItemMeta(Material.LEATHER_HELMET);
        meta.setColor(Color.RED);

        helmet.setItemMeta(meta);
        chestplate.setItemMeta(meta);
        leggings.setItemMeta(meta);
        boots.setItemMeta(meta);

        for (UUID uuid : inGame) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;
            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setHelmet(helmet.clone());
            inventory.setChestplate(chestplate.clone());
            inventory.setLeggings(leggings.clone());
            inventory.setBoots(boots.clone());
        }

        meta.setColor(Color.GREEN);
        helmet.setItemMeta(meta);
        chestplate.setItemMeta(meta);
        leggings.setItemMeta(meta);
        boots.setItemMeta(meta);

        for (PotionEffect potionEffect : infectedPlayer.getActivePotionEffects())
            infectedPlayer.removePotionEffect(potionEffect.getType());
        PlayerInventory inventory = infectedPlayer.getInventory();
        inventory.clear();
        inventory.setHelmet(helmet.clone());
        inventory.setChestplate(chestplate.clone());
        inventory.setLeggings(leggings.clone());
        inventory.setBoots(boots.clone());

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
    public String getName() {
        return "tag";
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

            stats.getActiveKit().getKit().beginApplyKit(player);
            stats.getActiveKit().getKit().giveSoup(player, 32);

            ScoreboardInfo.getInstance().updatePlayer(player);
        }

        Player infectedPlayer = Bukkit.getPlayer(this.infected);

        begin = null;
        inGame = null;
        infected = null;

        TAG_ENDED.broadcast();

        if (infectedPlayer == null) return;

        infectedPlayer.getInventory().clear();
        infectedPlayer.getInventory().setHelmet(null);
        infectedPlayer.getInventory().setChestplate(null);
        infectedPlayer.getInventory().setLeggings(null);
        infectedPlayer.getInventory().setBoots(null);

        KitPvPStats stats = KitPvP.getInstance().getStats(infectedPlayer);

        stats.getActiveKit().getKit().beginApplyKit(infectedPlayer);
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

        Player player = event.getPlayer();
        inGame.remove(player.getUniqueId());

        if (this.infected.equals(player.getUniqueId())) {
            player.getInventory().clear();

            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);

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

                if (!inGame.contains(damagee.getUniqueId()))
                    event.setCancelled(true);

                boolean remove = inGame.remove(damagee.getUniqueId());
                if (!remove) return;

                damagee.getInventory().clear();
                damagee.getInventory().setHelmet(null);
                damagee.getInventory().setChestplate(null);
                damagee.getInventory().setLeggings(null);
                damagee.getInventory().setBoots(null);

                KitPvPStats stats = KitPvP.getInstance().getStats(damagee);
                stats.getActiveKit().getKit().beginApplyKit(damagee);
                stats.getActiveKit().getKit().giveSoup(damagee, 32);

                KitPvPStats damagerStats = KitPvP.getInstance().getStats(damager);
                damagerStats.setCoins(damagerStats.getCoins() + 15);

                ScoreboardInfo.getInstance().updatePlayer(damager);

            } else if (inGame.contains(damagee.getUniqueId()) || inGame.contains(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        if (begin != null && (inGame.contains(uuid) || infected == uuid) &&
                event.getClickedInventory().getType() == InventoryType.PLAYER)
            event.setCancelled(true);
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

        TAG_ENDED.broadcast();
    }

    public static TagEvent getInstance() {
        return instance;
    }

    public Long getBegin() {
        return begin;
    }
}
