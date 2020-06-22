package net.skycade.kitpvp.events;

import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPEventStartEvent;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static net.skycade.kitpvp.Messages.*;

public class KillTheKingEvent extends RandomEvent implements Listener {
    private UUID king = null;
    private Long begin = null;
    private List<UUID> inGame = null;
    private BukkitRunnable task;
    private KitType initialKit;
    private Set<UUID> participated = new HashSet<>();

    private static KillTheKingEvent instance;

    private int prizeAmount = 5;
    private int participationAmount = 1;

    private ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
    private ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
    private ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
    private ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
    private ItemStack rod = new ItemStack(Material.BLAZE_ROD);
    @Override
    public int getFrequencyPerDay() {
        return 8;
    }

    public KillTheKingEvent() {
        super();
        Bukkit.getServer().getPluginManager().registerEvents(this, KitPvP.getInstance());
    }

    public void run(){
        instance = this;
        List<Player> p = Bukkit.getOnlinePlayers()
                .stream().filter(e ->
                        !KitPvP.getInstance().isInSpawnArea(e)
                                && !VanishStatus.isVanished(e.getUniqueId())
                ).collect(Collectors.toList());

        if (p.isEmpty()) {
            super.end();
            return;
        }

        this.king = p.get(ThreadLocalRandom.current().nextInt(p.size())).getUniqueId();
        begin = System.currentTimeMillis();

        Player kingPlayer = Bukkit.getPlayer(this.king);

        KitPvPEventStartEvent eventStartEvent = new KitPvPEventStartEvent(kingPlayer);
        Bukkit.getServer().getPluginManager().callEvent(eventStartEvent);

        KILLTHEKING_START.broadcast("%player%", kingPlayer.getName());
        for(Player pl: Bukkit.getOnlinePlayers()){
            pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
        }

        inGame = p.stream().map(Entity::getUniqueId).filter(e -> !e.equals(king)).collect(Collectors.toList());

        itemBuilder();

        for (PotionEffect potionEffect : kingPlayer.getActivePotionEffects())
            kingPlayer.removePotionEffect(potionEffect.getType());
        PlayerInventory inventory = kingPlayer.getInventory();
        inventory.clear();
        inventory.setHelmet(helmet.clone());
        inventory.setChestplate(chestplate.clone());
        inventory.setLeggings(leggings.clone());
        inventory.setBoots(boots.clone());
        inventory.addItem(rod.clone());

        KitPvPStats stats = KitPvP.getInstance().getStats(kingPlayer);
        initialKit = stats.getActiveKit().getKit().getKitType();
        stats.setActiveKit(KitType.DEFAULT);
        stats.getActiveKit().getKit().giveSoup(kingPlayer, 32);
        kingPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
        kingPlayer.updateInventory();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (inGame == null || begin == null) {
                    cancel();
                    return;
                }

                if (inGame.isEmpty() || System.currentTimeMillis() - begin > 5 * 60 * 1000L) {
                    KILLTHEKING_TOO_LONG.broadcast();

                    //Dish out event token rewards for participants
                    participated.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> {
                        KitPvPStats stats = KitPvP.getInstance().getStats(player);
                        if (stats != null) {
                            stats.giveEventTokens(participationAmount);
                            KILLTHEKING_PARTICIPATE.msg(player, "%amount%", Integer.toString(participationAmount));
                        }
                    });

                    //Dish out event token rewards for the king
                    KitPvPStats kingStats = KitPvP.getInstance().getStats(Bukkit.getPlayer(king));
                    if (kingStats != null) {
                        kingStats.giveEventTokens(prizeAmount);
                        KILLTHEKING_SURVIVED.msg(Bukkit.getPlayer(king), "%amount%", Integer.toString(participationAmount));
                    }
                    end();
                    cancel();
                }
            }
        };
        task.runTaskTimer(KitPvP.getInstance(), 20L, 20L);
    }

    @Override
    public String getName() {
        return "killtheking";
    }

    @Override
    public void end() {
        super.end();

        Player kingPlayer = Bukkit.getPlayer(this.king);

        begin = null;
        inGame = null;
        king = null;

        KILLTHEKING_ENDED.broadcast();
        if (kingPlayer == null) return;


        for (PotionEffect potionEffect : kingPlayer.getActivePotionEffects())
            kingPlayer.removePotionEffect(potionEffect.getType());
        kingPlayer.getOpenInventory().close();
        kingPlayer.getInventory().clear();
        kingPlayer.getInventory().setHelmet(null);
        kingPlayer.getInventory().setChestplate(null);
        kingPlayer.getInventory().setLeggings(null);
        kingPlayer.getInventory().setBoots(null);

        KitPvPStats stats = KitPvP.getInstance().getStats(kingPlayer);

        stats.setActiveKit(initialKit);
        stats.getActiveKit().getKit().beginApplyKit(kingPlayer);
        stats.getActiveKit().getKit().giveSoup(kingPlayer, 32);

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (begin == null) return;

        Player died = event.getEntity();
        if (this.king.equals(died.getUniqueId())) {
            Player killer = null;
            //try to get the killer
            try {
                killer = died.getKiller();//Dish out event token rewards for killer
                KitPvPStats killerStats = KitPvP.getInstance().getStats(killer);
                if (killerStats != null) {
                    killerStats.giveEventTokens(prizeAmount);
                    KILLTHEKING_WON.msg(killer, "%amount%", Integer.toString(prizeAmount));
                }

                //Dish out event token rewards for participants
                Player finalKiller = killer;
                participated.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> {
                    if (!(player == finalKiller)) {
                        KitPvPStats stats = KitPvP.getInstance().getStats(player);
                        if (stats != null) {
                            stats.giveEventTokens(participationAmount);
                            KILLTHEKING_PARTICIPATE.msg(player, "%amount%", Integer.toString(participationAmount));
                        }
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (Bukkit.getOfflinePlayer(king).isOnline()) {
                //Dish out event token rewards for the king
                KitPvPStats kingStats = KitPvP.getInstance().getStats(Bukkit.getPlayer(king));
                if (kingStats != null) {
                    kingStats.giveEventTokens(prizeAmount);
                    KILLTHEKING_PARTICIPATE.msg(Bukkit.getPlayer(king), "%amount%", Integer.toString(participationAmount));
                }
            }

            if (killer != null)
                KILLTHEKING_KILLED_BY.broadcast("%player%", killer.getName());
            else
                KILLTHEKING_KILLED.broadcast();

            stop();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (begin == null) return;

        Player player = event.getPlayer();
        if (this.king.equals(player.getUniqueId())) {
            player.getOpenInventory().close();
            player.getInventory().clear();

            for (PotionEffect potionEffect : player.getActivePotionEffects())
                player.removePotionEffect(potionEffect.getType());
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);

            KILLTHEKING_LOGGED_OUT.broadcast();
            stop();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMoveEvent(PlayerMoveEvent event){
        if (begin == null) return;

        Player player = event.getPlayer();
        if (this.king.equals(player.getUniqueId())){
            particleMoveEffect(player, ParticleEffect.CRIT, 1, 30);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event){
        if (begin == null) return;

        Player player = Bukkit.getPlayer(event.getEntity().getUniqueId());
        if (player != null && this.king.equals(player.getUniqueId())){
            participated.add(event.getDamager().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if(KitPvP.getInstance().getSpawnRegion().contains(event.getTo()) && event.getPlayer().getUniqueId().equals(this.king)) {
            KILLTHEKING_TO_SPAWN.broadcast();
            stop();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (begin == null || !this.king.equals(event.getWhoClicked().getUniqueId())) return;

        // stops king from removing their armor
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }

    public void remove(UUID uuid) {
        inGame.remove(uuid);
    }

    private void stop() {
        begin = null;
        inGame = null;
        king = null;
        if (task != null) task.cancel();
        super.end();

        KILLTHEKING_ENDED.broadcast();
    }

    private void itemBuilder(){
        ItemMeta helmetEnchantMeta = helmet.getItemMeta();
        helmetEnchantMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        helmetEnchantMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        helmet.setItemMeta(helmetEnchantMeta);

        ItemMeta chestplateEnchantMeta = chestplate.getItemMeta();
        chestplateEnchantMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        chestplateEnchantMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        chestplate.setItemMeta(chestplateEnchantMeta);

        ItemMeta leggingsEnchantMeta = leggings.getItemMeta();
        leggingsEnchantMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        leggingsEnchantMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        leggings.setItemMeta(leggingsEnchantMeta);

        ItemMeta bootsEnchantMeta = boots.getItemMeta();
        bootsEnchantMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        bootsEnchantMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        boots.setItemMeta(bootsEnchantMeta);

        ItemMeta rodEnchantMeta = rod.getItemMeta();
        rodEnchantMeta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
        rodEnchantMeta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        rod.setItemMeta(rodEnchantMeta);
    }

    private void particleMoveEffect(Player p, ParticleEffect effect, float radius, int particleAmount) {
        if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
            return;

        if (UtilPlayer.isMoving(p))
            for (int y = 0; y < 5; y++)
                Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                    if (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
                        particleCircle(p.getLocation().add(0, 0.1F, 0), radius, particleAmount, effect);
                }, y * 2);
    }

    private void particleCircle(Location location, float radius, int particleAmount, ParticleEffect effect) {
        for (int i = 0; i < particleAmount; i++) {
            double angle, x, z;
            angle = 2 * Math.PI * i / particleAmount;
            x = Math.cos(angle) * radius;
            z = Math.sin(angle) * radius;
            location.add(x, 0, z);
            effect.display(0.03F, 0.02F, 0.03F, 0.05F, 1, location, 40);
            location.subtract(x, 0, z);
        }
    }

    public static KillTheKingEvent getInstance() {
        return instance;
    }

    public UUID getCurrentKing(){
        return king;
    }
}
