package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class KitWizard extends Kit {

    private final HashMap<UUID, Location> lastWizardLoc = new HashMap<>();
    private final List<UUID> rodUse = new ArrayList<>();

    public KitWizard(KitManager kitManager) {
        super(kitManager, "Wizard", KitType.WIZARD, 34000, "You're a wizard Harry!");
        onMove();

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "REDSTONE_TORCH_ON");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 34000);

        defaultsMap.put("inventory.stick.enchantments.damage-all", 5);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 12);
        defaultsMap.put("armor.enchantments.protection", 3);

        setConfigDefaults(defaultsMap);

        if (getConfig().getString("kit.icon.material") != null) {
            if (getConfig().getString("kit.icon.material").contains("LEATHER")) {
                setIcon(new ItemBuilder(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase()))
                        .setColour(getColor(getConfig().getString("kit.icon.color"))).build());
            } else {
                setIcon(new ItemStack(Material.getMaterial(getConfig().getString("kit.icon.material").toUpperCase())));
            }
        } else {
            setIcon(new ItemStack(Material.DIRT));
        }
        setPrice(getConfig().getInt("kit.price"));
    }

    @Override
    public void applyKit(Player p, int level) {
        p.getInventory().addItem(new ItemBuilder(
                Material.STICK)
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.stick.enchantments.damage-all")).build());

        p.getInventory().addItem(new ItemBuilder(
                Material.BOOK).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.fromBGR(255, 153, 153)));
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        int level = getLevel(p);

        if (item.getType() == Material.BOOK) {
            if (!lastWizardLoc.containsKey(p.getUniqueId()))
                return;
            if (lastWizardLoc.get(p.getUniqueId()).distance(p.getLocation()) > 30 || getKitManager().getKitPvP().getSpawnRegion().contains(lastWizardLoc.get(p.getUniqueId())))
                return;
            if (!addCooldown(p, getName() + " teleport", 16, true))
                return;
            final Vector dir = p.getLocation().getDirection();
            Location newLoc = lastWizardLoc.get(p.getUniqueId());
            newLoc.setDirection(dir);

            p.teleport(newLoc);
            p.getLocation().setDirection(dir);
            p.sendMessage(ChatColor.DARK_PURPLE + "Woosh!");
            p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);
            p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

        } else if (item.getType() == Material.STICK) {
            if (rodUse.contains(p.getUniqueId()))
                return;
            rodUse.add(p.getUniqueId());
            Bukkit.getScheduler().runTaskLater(getKitManager().getPlugin(), () -> rodUse.remove(p.getUniqueId()), 15 * 20);

            new BukkitRunnable() {
                Location loc = p.getLocation();
                Vector dir = p.getLocation().getDirection().normalize();
                double t = 0.0;

                public void run() {
                    t += 0.05F;
                    double x = dir.getX() * t;
                    double y = dir.getY() * t;
                    double z = dir.getZ() * t;
                    loc.add(x, y, z);

                    ParticleEffect.LAVA.display(0, 0, 0, 0, 3, loc, 100);

                    for (Player target : UtilPlayer.getNearbyPlayers(loc, 1.5).stream().filter(player -> !player.equals(p) && player.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList()))
                        if (target.getGameMode() == GameMode.SURVIVAL)
                            target.damage(8 + (3 * 3), p);

                    if (t > 1.6)
                        this.cancel();
                }
            }.runTaskTimerAsynchronously(getKitManager().getPlugin(), 0, 1);
        }
    }

    private void onMove() {
        Bukkit.getScheduler().runTaskTimer(getKitManager().getKitPvP(), () -> getAllMovingPlayers().stream().filter(p -> getKitManager().getKitPvP().getStats(p).getActiveKit() == KitType.WIZARD && p.getItemInHand().getType() == Material.BOOK).collect(Collectors.toList()).forEach(p -> {
            Block targetBlock = getTargetBlock(p, true, 30);
            if (targetBlock == null)
                return;
            if (targetBlock.getLocation().add(0, 1, 0).getBlock().getType() != Material.AIR || targetBlock.getLocation().add(0, 2, 0).getBlock().getType() != Material.AIR)
                return;
            lastWizardLoc.put(p.getUniqueId(), targetBlock.getLocation().add(0, 1, 0));
            particleCircle(targetBlock.getLocation().add(0, 1.1F, 0), 1, 30, ParticleEffect.CRIT);
        }), 2, 2);
    }

    private Block getTargetBlock(Player p, boolean face, int length) {
        List<Block> blocks = getLineOfSight(p, length);
        for (Block block : blocks)
            if (block.getType() != Material.AIR)
                return block;
        return null;
    }

    private List<Block> getLineOfSight(Player p, int length) {
        return p.getLineOfSight(new HashSet<Material>(Arrays.asList(Material.values())), length);
    }

    @Override
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7Use your wand to shoot fire", "§7use the book to teleport to the", "§7particle location");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        lastWizardLoc.remove(e.getPlayer().getUniqueId());
        rodUse.remove(e.getPlayer().getUniqueId());
    }
}
