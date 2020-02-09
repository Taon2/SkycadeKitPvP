package net.skycade.kitpvp.kit;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.skycade.SkycadeCore.utility.CoreUtil;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.nms.ActionBarUtil;
import net.skycade.kitpvp.runnable.ItemRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

import static net.skycade.kitpvp.Messages.*;

public abstract class Kit implements Listener {

    private final Map<UUID, List<Long>> cooldownDate = new HashMap<>();
    private final Map<UUID, List<String>> playerCooldown = new HashMap<>();

    protected final Map<UUID, List<ItemRunnable>> playerItemRunnable = new HashMap<>();

    protected static final Map<UUID, Map<Location, BlockState>> frozenPlayers = new HashMap<>();
    protected static final List<UUID> frozenImmunity = new ArrayList<>();

    protected final List<UUID> shacoHit = new ArrayList<>();

    private static final CraftPlayer DUMMY_PLAYER = new CraftPlayer((CraftServer) Bukkit.getServer(),
            new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(),
                    ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(), new GameProfile(UUID.randomUUID(), ""),
                    new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle())));

    private final KitManager kitManager;
    private final String name;
    private final KitType type;
    private int price;
    private final boolean enabled;
    private final List<String> description;

    private ItemStack icon;

    public Kit(KitManager kitManager, String name, KitType type, List<String> description) {
        this(kitManager, name, type, 0, description);
    }

    public Kit(KitManager kitManager, String name, KitType type, int price, List<String> description) {
        this(kitManager, name, type, price, true, description);
    }

    public Kit(KitManager kitManager, String name, KitType type, int price, boolean enabled, List<String> description) {
        this.kitManager = kitManager;
        this.name = name;
        this.type = type;
        this.price = price;
        this.enabled = enabled;
        this.description = description;
}

    public void beginApplyKit(Player p) {
        if (p == null || !p.isOnline()) return;
        p.getOpenInventory().close();
        clearArmor(p);
        p.getInventory().clear();
        for (PotionEffect potionEffect : p.getActivePotionEffects()) p.removePotionEffect(potionEffect.getType());
        applyKit(p);
    }

    public abstract void applyKit(Player p);

    public KitManager getKitManager() {
        return kitManager;
    }

    public String getName() {
        return name;
    }

    public KitType getKitType() {
        return type;
    }

    public int getPrice() {
        return price / 2;
    }

    public void setPrice(int value) {
        this.price = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        if (icon == null) {
            applyKit(DUMMY_PLAYER);
            icon = DUMMY_PLAYER.getInventory().getItem(0);
            DUMMY_PLAYER.getInventory().clear();
            if (icon == null)
                icon = new ItemStack(Material.WOOD_SWORD);
        }
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setIcon(Material icon) {
        this.icon = new ItemStack(icon);
    }

    public boolean isActive(Player p) {
        return KitPvP.getInstance().getStats(p).getActiveKit() == type;
    }

    public void onDamageDealHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
    }

    public void onDamageGetHit(EntityDamageByEntityEvent event, Player damager, Player damagee) {
    }

    public void onItemUse(Player p, ItemStack item) {
    }

    public void onItemUse(Player p, ItemStack item, Block clickedBlock) {
    }

    public void onBlockPlace(Player p, Block block, BlockState replaced) {
    }

    public void onBlockBreak(Player p, Block block) {
    }

    public void onInteract(Player p, Player target, ItemStack item) {
    }

    public void onMove(Player p) {
    }

    public boolean onDeath(Player died, Player killer) {
        return true;
    }

    public void removeSummon(int seconds, Player p) {
    }

    public void onArrowLand(Player p, Block block, ProjectileHitEvent event) {
    }

    public void cancelRunnables(Player p) {
    }

    public void reimburseItem(Player p, ItemStack item){
    }

    protected boolean onCooldown(Player p, String ability) {
        if (playerCooldown.containsKey(p.getUniqueId()) && playerCooldown.get(p.getUniqueId()).contains(ability)) {
            if (cooldownDate.containsKey(p.getUniqueId())) {
                long remainingSeconds = (cooldownDate.get(p.getUniqueId()).get(playerCooldown.get(p.getUniqueId()).indexOf(ability)) - new Date().getTime()) / 1000;

                ActionBarUtil.sendActionBarMessage(p, ON_COOLDOWN.getMessage()
                        .replace("%time%", CoreUtil.niceFormat((int) remainingSeconds))
                        .replace("%thing%", ability),
                        4, KitPvP.getInstance());
                return true;
            } else {
                ActionBarUtil.sendActionBarMessage(p, ON_COOLDOWN_NO_TIME.getMessage()
                                .replace("%thing%", ability),
                        4, KitPvP.getInstance());
                return true;
            }
        }

        return false;
    }

    protected void removeCooldowns(Player p, String ability) {
        if (playerCooldown.get(p.getUniqueId()).indexOf(ability) != -1) {
            cooldownDate.get(p.getUniqueId()).remove(playerCooldown.get(p.getUniqueId()).indexOf(ability));
            playerCooldown.get(p.getUniqueId()).remove(ability);
        }
    }

    public boolean addCooldown(Player p, String ability, int seconds, boolean message) {
        if (onCooldown(p, ability)) {
            return false;
        }
        List<String> cooldowns = playerCooldown.get(p.getUniqueId()) == null ? new ArrayList<>() : playerCooldown.get(p.getUniqueId());
        List<Long> cooldownDates = cooldownDate.get(p.getUniqueId()) == null ? new ArrayList<>() : cooldownDate.get(p.getUniqueId());
        cooldowns.add(ability);
        cooldownDates.add(new Date().getTime() + seconds * 1000);
        playerCooldown.put(p.getUniqueId(), cooldowns);
        cooldownDate.put(p.getUniqueId(), cooldownDates);

        // Send cooldown message
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            if (playerCooldown.get(p.getUniqueId()).contains(ability)){
                if (message)
                    ActionBarUtil.sendActionBarMessage(p, OFF_COOLDOWN.getMessage()
                                    .replace("%thing%", ability),
                            4, KitPvP.getInstance());
                removeCooldowns(p, ability);
            }
        }, seconds * 20);

        // Show remaining seconds as level
        p.setLevel(seconds);
        new BukkitRunnable() {
            public void run() {
                if (playerCooldown.get(p.getUniqueId()).contains(ability) && p.isOnline()) {
                    if (p.getLevel() > 0)
                        p.setLevel(p.getLevel() - 1);
                } else {
                    p.setLevel(0);
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(KitPvP.getInstance(), 0, 20);
        return true;
    }

    protected boolean isValidBlock(Material type) {
        return Arrays
                .asList(Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA,
                        Material.YELLOW_FLOWER, Material.GRASS, Material.LONG_GRASS, Material.WEB,
                        Material.ACTIVATOR_RAIL, Material.POWERED_RAIL, Material.RAILS, Material.DETECTOR_RAIL)
                .contains(type);
    }

    private void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public void giveSoup(Player p, int amount) {
        if (p == null || !p.isOnline()) return;
        KitType activeKit = KitPvP.getInstance().getStats(p).getActiveKit();

        for (int x = 0; x < amount; x++) {
            if (p.getInventory().firstEmpty() == -1)
                break;

            if (activeKit == KitType.POTIONMASTER || activeKit == KitType.BUILDUHC || activeKit == KitType.WITCHDOCTOR) {
                p.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 16421));
            } else if (activeKit == KitType.HULK) {
                p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
            } else {
                p.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
            }
        }

        if (activeKit == KitType.HULK) {
            p.getInventory().setItem(0, null);
        }
    }

    protected boolean teleportBehindPlayer(Player p, Location damageeLoc) {
        double nX;
        double nZ;
        float nang = damageeLoc.getYaw() + 90;
        if (nang < 0)
            nang += 360;
        nX = Math.cos(Math.toRadians(nang));
        nZ = Math.sin(Math.toRadians(nang));

        Location newDamagerLoc = new Location(damageeLoc.getWorld(), damageeLoc.getX() - nX, damageeLoc.getY(),
                damageeLoc.getZ() - nZ, damageeLoc.getYaw(), damageeLoc.getPitch());

        if (!isValidBlock(newDamagerLoc.getBlock().getType()))
            if (newDamagerLoc.getBlock().getType() != Material.AIR && newDamagerLoc.add(0, 1, 0).getBlock().getType() != Material.AIR)
                return false;

        newDamagerLoc.setDirection(new Vector(newDamagerLoc.getDirection().getX(),
                p.getLocation().getDirection().getY(), newDamagerLoc.getDirection().getZ()));

        p.teleport(newDamagerLoc);
        return true;
    }

    protected void startItemRunnable(Player p, int seconds, ItemStack item, int maxAmount, KitType kitType) {
        ItemRunnable runnable = new ItemRunnable(KitPvP.getInstance(), seconds, p, item, maxAmount, kitType);
        if (playerItemRunnable.containsKey(p.getUniqueId()))
            playerItemRunnable.get(p.getUniqueId()).add(runnable);
        else {
            List<ItemRunnable> runnables = new ArrayList<>();
            runnables.add(runnable);
            playerItemRunnable.put(p.getUniqueId(), runnables);
        }
    }

    public void stopItemRunnables(Player p) {
        if (!playerItemRunnable.containsKey(p.getUniqueId()))
            return;

        playerItemRunnable.get(p.getUniqueId()).forEach(ItemRunnable::stopRunnable);
    }

    protected void freezePlayer(Player p, int sec) {
        frozenPlayers.remove(p.getUniqueId());

        if (!p.isOnGround()) {
            double y = Math.floor(p.getLocation().getY());
            while (!p.isOnGround()) {
                Location loc = p.getLocation();
                loc.setY(y);
                if (loc.getBlock().getType() == Material.AIR) {
                    y--;
                } else {
                    p.teleport(new Location(loc.getWorld(), Math.floor(loc.getX()) + .5, y+1, Math.floor(loc.getZ()) + .5, loc.getYaw(), loc.getPitch()));
                    break;
                }
            }
        }

        Location loc = p.getLocation();
        BlockState replaced = loc.getBlock().getState();
        loc.getBlock().setType(Material.ICE);

        Map<Location, BlockState> ice = new HashMap<>();
        ice.put(loc, replaced);

        frozenPlayers.put(p.getUniqueId(), ice);
        frozenImmunity.add(p.getUniqueId());

        p.teleport(new Location(loc.getWorld(), Math.floor(loc.getX()) + .5, loc.getY(), Math.floor(loc.getZ()) + .5, loc.getYaw(), loc.getPitch()));

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            loc.getBlock().setType(replaced.getType());
            BlockState blockState = loc.getBlock().getState();
            blockState.setData(replaced.getData());
            blockState.update();

            frozenPlayers.remove(p.getUniqueId());
            YOURE_UNFROZEN.msg(p);
        }, sec * 20);

        // Removes players from the list 5 seconds after being unfrozen, to stop players from being frozen right away
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            frozenImmunity.remove(p.getUniqueId());
        }, (sec + 5) * 20);
    }

    public List<UUID> getFrozenImmunity() {
        return frozenImmunity;
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
            frozenPlayers.get(event.getPlayer().getUniqueId()).forEach((loc, replaced) -> {
                loc.getBlock().setType(replaced.getType());
                BlockState blockState = loc.getBlock().getState();
                blockState.setData(replaced.getData());
                blockState.update();
            });

            frozenPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
            event.getTo().setX(event.getFrom().getX());
            event.getTo().setY(event.getFrom().getY());
            event.getTo().setZ(event.getFrom().getZ());
        }
    }

    protected void particleMoveEffect(Player p, ParticleEffect effect, float radius, int particleAmount) {
        if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
            return;

        if (UtilPlayer.isMoving(p))
            for (int y = 0; y < 5; y++)
                Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                    if (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
                        particleCircle(p.getLocation().add(0, 0.1F, 0), radius, particleAmount, effect);
                }, y * 2);
    }

    protected void particleCircle(Location location, float radius, int particleAmount, ParticleEffect effect) {
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

    protected void particleTracerEffect(Player p, Color color, int particleAmount) {
        if (p.getLocation().subtract(0, 1, 0).getBlock().getType() == Material.AIR)
            return;
        if (UtilPlayer.isMoving(p))
            for (int y = 0; y < 6; y++)
                Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
                    for (int i = 0; i < particleAmount; i++)
                        ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(color), p.getLocation().add(0, 0.1F, 0), 1F);
                }, y * 2);
    }

    protected void shootParticlesFromLoc(Player p, ParticleEffect effect, int particleAmount, float radius) {
        for (int i = 0; i < particleAmount; i++)
            effect.display(radius, radius, radius, i / 1000 < 0.2 ? 0.2F : i / particleAmount, 1, p.getLocation(), 40);
    }

    protected List<Player> getAllMovingPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(UtilPlayer::isMoving).collect(Collectors.toList());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        frozenPlayers.remove(event.getEntity().getUniqueId());
    }

    public abstract List<String> getHowToObtain();
}