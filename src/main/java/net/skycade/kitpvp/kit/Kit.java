package net.skycade.kitpvp.kit;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.PlayerInteractManager;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.runnable.ItemRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.CraftServer;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class Kit implements Listener {

    private ConfigurationSection config = new YamlConfiguration();
    private final Map<UUID, Long> cooldownDate = new HashMap<>();
    private final Map<UUID, List<String>> playerCooldown = new HashMap<>();

    //For freeze
    protected final Map<UUID, Location> lastLocation = new HashMap<>();

    protected final Map<UUID, ItemRunnable> playerItemRunnable = new HashMap<>();

    protected final List<UUID> frozenPlayers = new ArrayList<>();
    protected final List<UUID> shacoHit = new ArrayList<>();

    boolean freezeRunning = false;

    private static final CraftPlayer DUMMY_PLAYER = new CraftPlayer((CraftServer) Bukkit.getServer(),
            new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(),
                    ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle(), new GameProfile(UUID.randomUUID(), ""),
                    new PlayerInteractManager(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle())));

    private final KitManager kitManager;
    private final String name;
    private final KitType type;
    private int price;
    private final boolean enabled;
    private final String[] description;

    private ItemStack icon;

    public Kit(KitManager kitManager, String name, KitType type, String... description) {
        this(kitManager, name, type, 0, description);
    }

    public Kit(KitManager kitManager, String name, KitType type, int price, String... description) {
        this(kitManager, name, type, price, true, description);
    }

    public Kit(KitManager kitManager, String name, KitType type, int price, boolean enabled, String... description) {
        this.kitManager = kitManager;
        this.name = name;
        this.type = type;
        this.price = price;
        this.enabled = enabled;
        this.description = description;
    }

    public void applyKit(Player p) {
        if (p == null || !p.isOnline()) return;
        p.getInventory().clear();
        for (PotionEffect potionEffect : p.getActivePotionEffects()) p.removePotionEffect(potionEffect.getType());
        applyKit(p, getLevel(p));
    }

    public abstract void applyKit(Player p, int level);

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

    public String[] getDescription() {
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

    public int getLevel(Player p) {
        if (p.equals(DUMMY_PLAYER))
            return 1;
        try {
            return KitPvP.getInstance().getStats(p).getKits().get(getKitType()).getLevel();
        } catch (Exception e) {
            return 1;
        }
    }

    public boolean isActive(Player p) {
        return KitPvP.getInstance().getStats(p).getActiveKit() == type;
    }

    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
    }

    public void onDamageGetHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
    }

    public void onItemUse(Player p, ItemStack item) {
    }

    public void onInteract(Player p, Player target, ItemStack item) {
    }

    public void onMove(Player p) {
    }

    public List<String> getAbilityDesc() {
        return null;
    }

    public boolean onCooldown(Player p, String ability) {
        if (playerCooldown.containsKey(p.getUniqueId()) && playerCooldown.get(p.getUniqueId()).contains(ability))
            return true;
        return false;
    }

    protected void removeCooldowns(Player p) {
        playerCooldown.remove(p.getUniqueId());
        cooldownDate.remove(p.getUniqueId());
    }

    public boolean addCooldown(Player p, String ability, int seconds, boolean message) {
        if (onCooldown(p, ability)) {
            if (cooldownDate.containsKey(p.getUniqueId())) {
                Long remainingSeconds = (cooldownDate.get(p.getUniqueId()) - new Date().getTime()) / 1000;
                p.sendMessage("§cAbility is on cooldown, wait " + remainingSeconds + " seconds.");
            } else
                p.sendMessage("§cAbility is on cooldown.");
            return false;
        }
        List<String> cooldowns = playerCooldown.get(p.getUniqueId()) == null ? new ArrayList<>() : playerCooldown.get(p.getUniqueId());
        cooldowns.add(ability);
        playerCooldown.put(p.getUniqueId(), cooldowns);
        cooldownDate.put(p.getUniqueId(), new Date().getTime() + seconds * 1000);

        // From cooldown messsage
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            if (playerCooldown.containsKey(p.getUniqueId())) {
                if (message)
                    p.sendMessage("§7You can now use §a" + ability + "§7.");
                removeCooldowns(p);
            }
        }, seconds * 20);

        // Show remaining seconds as level
        p.setLevel(seconds + 1);
        new BukkitRunnable() {
            public void run() {
                if (playerCooldown.containsKey(p.getUniqueId())) {
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

    public boolean isValidBlock(Material type) {
        return Arrays
                .asList(Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA,
                        Material.YELLOW_FLOWER, Material.GRASS, Material.LONG_GRASS, Material.WEB,
                        Material.ACTIVATOR_RAIL, Material.POWERED_RAIL, Material.RAILS, Material.DETECTOR_RAIL)
                .contains(type);
    }

    public ItemStack[] getArmour(Material mat, int durability, int protection) {
        return getArmour(mat, durability, protection, null);
    }

    public ItemStack[] getArmour(Material mat, int durability, int protection, Color colour) {
        String material = mat.toString().split("_")[0];
        List<ItemStack> armour = new ArrayList<>();
        for (String type : Arrays.asList("BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET"))
            armour.add(new ItemBuilder(Material.getMaterial(material + "_" + type)).setColour(colour)
                    .addEnchantment(Enchantment.DURABILITY, durability)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection).build());
        return armour.toArray(new ItemStack[armour.size()]);
    }

    public void giveSoup(Player p, int amount) {
        if (p == null || !p.isOnline()) return;
        for (int x = 0; x < amount; x++) {
            if (p.getInventory().firstEmpty() == -1)
                break;
            p.getInventory().addItem(
                    KitPvP.getInstance().getStats(p).getActiveKit() == KitType.POTIONMASTER
                            ? new ItemStack(Material.POTION, 1, (short) 16421)
                            : new ItemStack(Material.MUSHROOM_SOUP, 1));
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
            if (newDamagerLoc.getBlock().getType() != Material.AIR  && newDamagerLoc.add(0, 1, 0).getBlock().getType() != Material.AIR)
                return false;

        newDamagerLoc.setDirection(new Vector(newDamagerLoc.getDirection().getX(),
                p.getLocation().getDirection().getY(), newDamagerLoc.getDirection().getZ()));

        p.teleport(newDamagerLoc);
        return true;
    }

    protected void startItemRunnable(Player p, int seconds, ItemStack item, int maxAmount, KitType kitType) {
        if (playerItemRunnable.containsKey(p.getUniqueId()))
            playerItemRunnable.get(p.getUniqueId()).stopRunnable();
        ItemRunnable runnable = new ItemRunnable(KitPvP.getInstance(), seconds, p, item, maxAmount, kitType);
        playerItemRunnable.put(p.getUniqueId(), runnable);
    }

    protected void freezePlayer(Player p, int sec) {
        if (!freezeRunning) {
            onFreezeMove();
        }
        
        frozenPlayers.remove(p.getUniqueId());
        lastLocation.remove(p.getUniqueId());

        frozenPlayers.add(p.getUniqueId());
        lastLocation.put(p.getUniqueId(), p.getLocation());
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            frozenPlayers.remove(p.getUniqueId());
            p.sendMessage("§bYou are now unfrozen.");
            lastLocation.remove(p);
        }, sec * 20);
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

    private void onFreezeMove() {
        freezeRunning = true;
        Bukkit.getScheduler().runTaskTimer(KitPvP.getInstance(), () -> frozenPlayers.forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                if (!lastLocation.containsKey(p.getUniqueId())) {
                    if (p.getLocation().getBlock().getType() != Material.AIR)
                        lastLocation.put(p.getUniqueId(), p.getLocation());
                } else {
                    if (lastLocation.get(p.getUniqueId()).distance(p.getLocation()) > 0.2) {
                        final Vector dir = p.getLocation().getDirection();
                        Location newLoc = lastLocation.get(p.getUniqueId());
                        newLoc.setDirection(dir);
                        p.teleport(newLoc);
                    }

                }
            }
        }), 10, 10);
    }

    protected List<Player> getAllMovingPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(UtilPlayer::isMoving).collect(Collectors.toList());
    }

    public int getLevelUpXp(Player p) {
        return (getPrice() * getLevel(p) / 20) * KitPvP.getInstance().getConfig().getInt("required-xp-multiplier");
    }

    public void increaseXp(Player p, int xp) {
        /*
        KitPvPStats stats = kitManager.getKitPvP().getStats(p);

        // KitMaster can't level up
        if (stats.getActiveKit() == KitType.KITMASTER)
            return;

        KitData data = stats.getKits().get(stats.getActiveKit());
        if (data == null || data.getLevel() >= 3)
            return;
        data.setXp(data.getXp() + xp);

        // Level up
        if (data.getXp() >= getLevelUpXp(p)) {
            int difference = data.getXp() - getLevelUpXp(p) ;
            data.setLevel(data.getLevel() + 1);
            data.setXp(difference);
            p.sendMessage("§7Your kit §alevel increased §7to " + data.getLevel() + "!");
            applyKit(p);
            stats.getActiveKit().getKit().giveSoup(p, 30);
        } */ // no leveling up
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        frozenPlayers.remove(e.getEntity().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        frozenPlayers.remove(e.getPlayer().getUniqueId());
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public void setConfigDefaults(Map<String, Object> defaultsMap) {
        File configFile = new File(KitPvP.getInstance().getDataFolder(), "kits.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection kitSection = config.getConfigurationSection(getName());
        if (kitSection == null) {
            kitSection = new YamlConfiguration();
            for (Map.Entry<String, Object> entry : defaultsMap.entrySet()) {
                kitSection.set(entry.getKey(), entry.getValue());
            }
        }

        if (defaultsMap != null) {
            for (Map.Entry<String, Object> entry : defaultsMap.entrySet()) {
                if (kitSection.get(entry.getKey(), null) == null) {
                    kitSection.set(entry.getKey(), entry.getValue());
                }
            }
        }
        config.set(getName(), kitSection);
        try {
            config.save(configFile);
        } catch (IOException e) {
            KitPvP.getInstance().getLogger().log(Level.SEVERE, "An error occurred while trying to save kits.yml", e);
        }
        this.config = kitSection;
    }

    public void reloadConfig() {
        setConfigDefaults(null);
    }

    public Color getColor(String paramString) {
        String temp = paramString;
        if (temp.equalsIgnoreCase("AQUA")) return Color.AQUA;
        if (temp.equalsIgnoreCase("BLACK")) return Color.BLACK;
        if (temp.equalsIgnoreCase("BLUE")) return Color.BLUE;
        if (temp.equalsIgnoreCase("FUCHSIA")) return Color.FUCHSIA;
        if (temp.equalsIgnoreCase("GRAY")) return Color.GRAY;
        if (temp.equalsIgnoreCase("GREEN")) return Color.GREEN;
        if (temp.equalsIgnoreCase("LIME")) return Color.LIME;
        if (temp.equalsIgnoreCase("MAROON")) return Color.MAROON;
        if (temp.equalsIgnoreCase("NAVY")) return Color.NAVY;
        if (temp.equalsIgnoreCase("OLIVE")) return Color.OLIVE;
        if (temp.equalsIgnoreCase("ORANGE")) return Color.ORANGE;
        if (temp.equalsIgnoreCase("PURPLE")) return Color.PURPLE;
        if (temp.equalsIgnoreCase("RED")) return Color.RED;
        if (temp.equalsIgnoreCase("SILVER")) return Color.SILVER;
        if (temp.equalsIgnoreCase("TEAL")) return Color.TEAL;
        if (temp.equalsIgnoreCase("WHITE")) return Color.WHITE;
        if (temp.equalsIgnoreCase("YELLOW")) return Color.YELLOW;
        return null;
    }

}