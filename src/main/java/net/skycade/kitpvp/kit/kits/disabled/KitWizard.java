package net.skycade.kitpvp.kit.kits.disabled;

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

import static net.skycade.kitpvp.Messages.WOOSH;

public class KitWizard extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack book;

    private final HashMap<UUID, Location> lastWizardLoc = new HashMap<>();
    private final List<UUID> rodUse = new ArrayList<>();

    public KitWizard(KitManager kitManager) {
        super(kitManager, "Wizard", KitType.WIZARD, 34000, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.fromRGB(153, 153, 255)).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.fromRGB(153, 153, 255)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.fromRGB(153, 153, 255)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.fromRGB(153, 153, 255)).build();
        weapon = new ItemBuilder(
                Material.STICK)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5).build();
        book = new ItemStack(
                Material.BOOK);

        ItemStack icon = new ItemStack(Material.REDSTONE_TORCH_ON);
        setIcon(icon);

        onMove();
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(book);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
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
            WOOSH.msg(p);
            p.getWorld().playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 1);
            p.getWorld().playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

        } else if (item.getType() == Material.STICK) {
            if (rodUse.contains(p.getUniqueId()))
                return;
            rodUse.add(p.getUniqueId());
            Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> rodUse.remove(p.getUniqueId()), 15 * 20);

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
            }.runTaskTimerAsynchronously(getKitManager().getKitPvP(), 0, 1);
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        lastWizardLoc.remove(e.getPlayer().getUniqueId());
        rodUse.remove(e.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "You're a wizard Harry!",
                "",
                ChatColor.GRAY + "Shoots fire and teleports."
        );
    }
}
