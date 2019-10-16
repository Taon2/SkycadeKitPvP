package net.skycade.kitpvp.kit;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.skycade.SkycadeCore.utility.CoreUtil;
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
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.skycade.kitpvp.Messages.*;

public abstract class Kit implements Listener {

    private ConfigurationSection config = new YamlConfiguration();
    private final Map<UUID, List<Long>> cooldownDate = new HashMap<>();
    private final Map<UUID, List<String>> playerCooldown = new HashMap<>();

    protected final Map<UUID, ItemRunnable> playerItemRunnable = new HashMap<>();

    protected final List<UUID> frozenPlayers = new ArrayList<>();
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

    public void beginApplyKit(Player p) {
        if (p == null || !p.isOnline()) return;
        clearArmor(p);
        p.getInventory().clear();
        for (PotionEffect potionEffect : p.getActivePotionEffects()) p.removePotionEffect(potionEffect.getType());
        applyKit(p);
    }

    protected abstract void applyKit(Player p);

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

    protected boolean onCooldown(Player p, String ability) {
        return playerCooldown.containsKey(p.getUniqueId()) && playerCooldown.get(p.getUniqueId()).contains(ability);
    }

    protected void removeCooldowns(Player p, String ability) {
        cooldownDate.get(p.getUniqueId()).remove(playerCooldown.get(p.getUniqueId()).indexOf(ability));
        playerCooldown.get(p.getUniqueId()).remove(ability);
    }

    public boolean addCooldown(Player p, String ability, int seconds, boolean message) {
        if (onCooldown(p, ability)) {
            if (cooldownDate.containsKey(p.getUniqueId())) {
                long remainingSeconds = (cooldownDate.get(p.getUniqueId()).get(playerCooldown.get(p.getUniqueId()).indexOf(ability)) - new Date().getTime()) / 1000;

                ON_COOLDOWN.msg(p, "%time%", CoreUtil.niceFormat((int) remainingSeconds), "%thing%", ability);
            } else
                ON_COOLDOWN_NO_TIME.msg(p, "%thing%", ability);
            return false;
        }
        List<String> cooldowns = playerCooldown.get(p.getUniqueId()) == null ? new ArrayList<>() : playerCooldown.get(p.getUniqueId());
        List<Long> cooldownDates = cooldownDate.get(p.getUniqueId()) == null ? new ArrayList<>() : cooldownDate.get(p.getUniqueId());
        cooldowns.add(ability);
        cooldownDates.add(new Date().getTime() + seconds * 1000);
        playerCooldown.put(p.getUniqueId(), cooldowns);
        cooldownDate.put(p.getUniqueId(), cooldownDates);

        // From cooldown messsage
        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            if (playerCooldown.get(p.getUniqueId()).contains(ability)){
                if (message)
                    OFF_COOLDOWN.msg(p, "%thing%", ability);
                removeCooldowns(p, ability);
            }
        }, seconds * 20);

        // Show remaining seconds as level
        p.setLevel(seconds + 1);
        new BukkitRunnable() {
            public void run() {
                if (playerCooldown.get(p.getUniqueId()).contains(ability)) {
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

    private void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
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
            if (newDamagerLoc.getBlock().getType() != Material.AIR && newDamagerLoc.add(0, 1, 0).getBlock().getType() != Material.AIR)
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

    protected void reimburseItem(Player p, ItemStack item, int maxAmount, KitType kitType) {
        Inventory inv = p.getInventory();
        int amount = 0;

        Integer finalSlot = null;
        for (Integer i = 0; i < inv.getSize(); i++)
            if (inv.getItem(i) != null)
                if (inv.getItem(i).getType() == item.getType()) {
                    amount += inv.getItem(i).getAmount();
                    if (amount <= inv.getMaxStackSize())
                        finalSlot = i;
                }
        if (finalSlot != null && amount > 0 && KitPvP.getInstance().getStats(p).getActiveKit() == kitType) {
            ItemStack invItem = inv.getItem(finalSlot);
            if (amount < maxAmount)
                inv.setItem(finalSlot, new ItemStack(invItem.getType(), invItem.getAmount() + 1));
        } else
            p.getInventory().addItem(item);
    }

    protected void freezePlayer(Player p, int sec) {
        frozenPlayers.remove(p.getUniqueId());

        frozenPlayers.add(p.getUniqueId());

        if (!p.isOnGround()) {
            double y = Math.floor(p.getLocation().getY());
            while (!p.isOnGround()) {
                Location loc = p.getLocation();
                loc.setY(y);
                if (loc.getBlock().getType().equals(Material.AIR)) {
                    y--;
                } else {
                    p.teleport(new Location(loc.getWorld(), Math.floor(loc.getX()) + .5, y+1, Math.floor(loc.getZ()) + .5, loc.getYaw(), loc.getPitch()));
                    break;
                }
            }
        }

        Location loc = p.getLocation();
        Material initialType = loc.getBlock().getType();
        loc.getBlock().setType(Material.ICE);

        p.teleport(new Location(loc.getWorld(), Math.floor(loc.getX()) + .5, loc.getY(), Math.floor(loc.getZ()) + .5, loc.getYaw(), loc.getPitch()));

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            loc.getBlock().setType(initialType);
            frozenPlayers.remove(p.getUniqueId());
            YOURE_UNFROZEN.msg(p);
        }, sec * 20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
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
        if (paramString.equalsIgnoreCase("AQUA")) return Color.AQUA;
        if (paramString.equalsIgnoreCase("BLACK")) return Color.BLACK;
        if (paramString.equalsIgnoreCase("BLUE")) return Color.BLUE;
        if (paramString.equalsIgnoreCase("FUCHSIA")) return Color.FUCHSIA;
        if (paramString.equalsIgnoreCase("GRAY")) return Color.GRAY;
        if (paramString.equalsIgnoreCase("GREEN")) return Color.GREEN;
        if (paramString.equalsIgnoreCase("LIME")) return Color.LIME;
        if (paramString.equalsIgnoreCase("MAROON")) return Color.MAROON;
        if (paramString.equalsIgnoreCase("NAVY")) return Color.NAVY;
        if (paramString.equalsIgnoreCase("OLIVE")) return Color.OLIVE;
        if (paramString.equalsIgnoreCase("ORANGE")) return Color.ORANGE;
        if (paramString.equalsIgnoreCase("PURPLE")) return Color.PURPLE;
        if (paramString.equalsIgnoreCase("RED")) return Color.RED;
        if (paramString.equalsIgnoreCase("SILVER")) return Color.SILVER;
        if (paramString.equalsIgnoreCase("TEAL")) return Color.TEAL;
        if (paramString.equalsIgnoreCase("WHITE")) return Color.WHITE;
        if (paramString.equalsIgnoreCase("YELLOW")) return Color.YELLOW;
        return null;
    }

}