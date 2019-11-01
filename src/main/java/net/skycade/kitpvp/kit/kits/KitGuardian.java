package net.skycade.kitpvp.kit.kits;

import net.jaxonbrown.guardianBeam.beam.Beam;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPEventStartEvent;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitGuardian extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int fishCooldown = 20;

    private Map<UUID, Beam> beamMap = new HashMap<>();
    private Map<UUID, List<Integer>> beamRunnableMap = new HashMap<>();

    public KitGuardian(KitManager kitManager) {
        super(kitManager, "Guardian", KitType.GUARDIAN, 50000, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fires a laser at the nearest enemy within 6 blocks.")
                .setColour(Color.ORANGE).build();
        chestplate = new ItemBuilder(
                Material.DIAMOND_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.THORNS, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fires a laser at the nearest enemy within 6 blocks.").build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fires a laser at the nearest enemy within 6 blocks.")
                .setColour(Color.fromRGB(0, 204, 204)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 10)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .addEnchantment(Enchantment.DEPTH_STRIDER, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Fires a laser at the nearest enemy within 6 blocks.")
                .setColour(Color.fromRGB(0, 204, 204)).build();
        weapon = new ItemBuilder(
                Material.RAW_FISH)
                .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + fishCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "slows nearby enemies.").build();

        ItemStack icon = new ItemStack(Material.PRISMARINE_CRYSTALS);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        Beam beam = new Beam(p.getLocation(), p.getLocation(), 100.0, 1L);
        beam.start();

        int beamRunnable = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(KitPvP.getInstance(), new BukkitRunnable() {
            public void run() {
                beam.setStartingPosition(p.getLocation());

                if (!getKitManager().getKitPvP().getSpawnRegion().contains(p)) {
                    Set<Player> nearbyPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 6);
                    nearbyPlayers.remove(p);

                    if (!(nearbyPlayers.isEmpty())) {
                        Player target = getClosestTarget(nearbyPlayers, p);

                        if (areBlocksInWay(p.getLocation(), target.getLocation())) {
                            beam.setEndingPosition(p.getLocation());
                            return;
                        }

                        beam.setEndingPosition(target.getLocation());
                    }
                    else
                        beam.setEndingPosition(p.getLocation());
                } else {
                    beam.setEndingPosition(p.getLocation());
                }
            }
        }, 0L, 0L);

        int damageRunnable = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(KitPvP.getInstance(), new BukkitRunnable() {
            public void run() {
                Set<Player> nearbyPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 6);
                nearbyPlayers.remove(p);

                if (!(nearbyPlayers.isEmpty())) {
                    Player target = getClosestTarget(nearbyPlayers, p);

                    if (areBlocksInWay(p.getLocation().add(0, 1, 0), target.getLocation().add(0, 1, 0))) {
                        return;
                    }

                    if (beam.isViewing(target) && target != p) {
                        target.damage(4);
                    }
                }
            }
        }, 60L, 60L);

        List<Integer> runnables = new ArrayList<>();
        runnables.add(beamRunnable);
        runnables.add(damageRunnable);

        cancelRunnables(p);
        beamMap.put(p.getUniqueId(), beam);
        beamRunnableMap.put(p.getUniqueId(), runnables);
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.RAW_FISH)
            return;
        if (!addCooldown(p, getName(), fishCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Set<Player> targetPlayers = UtilPlayer.getNearbyPlayers(p.getLocation(), 7);
        if (targetPlayers.size() <= 1)
            removeCooldowns(p, getName());

        targetPlayers.forEach(target -> {
            if (target != p) {
                Packet<?> packet = new PacketPlayOutWorldParticles(EnumParticle.MOB_APPEARANCE, false, (float) target.getLocation().getX(), (float) target.getLocation().getY(), (float) target.getLocation().getZ(), 0F, 0F, 0F, 10, 1);
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(packet);

                target.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOW, 200, 1));
            }
        });
    }

    @EventHandler
    public void onEventStart(KitPvPEventStartEvent e) {
        cancelRunnables(e.getPlayer());
    }

    @Override
    public void cancelRunnables(Player p) {
        if (beamRunnableMap.containsKey(p.getUniqueId())) {
            List<Integer> runnables = beamRunnableMap.get(p.getUniqueId());

            runnables.forEach(id -> {
                Bukkit.getScheduler().cancelTask(id);
                beamRunnableMap.remove(p.getUniqueId());
            });
        }

        if (beamMap.containsKey(p.getUniqueId())) {
            Beam beam = beamMap.get(p.getUniqueId());
            beam.stop();
            beamMap.remove(p.getUniqueId());
        }
    }

    private Player getClosestTarget(Set<Player> players, Player p) {
        double distance = 100;
        Player closestPlayer = null;

        for (Player target : players)
            if (p.getLocation().distance(target.getLocation()) < distance)
                closestPlayer = target;
        return closestPlayer;
    }

    private boolean areBlocksInWay(Location loc1, Location loc2) {
        int lowX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int lowY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int lowZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        List<Material> blocks = new ArrayList<>();

        for(int x = 0; x <= Math.abs(loc1.getBlockX()-loc2.getBlockX()); x++){
            for(int y = 0; y <= Math.abs(loc1.getBlockY()-loc2.getBlockY()); y++){
                for(int z = 0; z <= Math.abs(loc1.getBlockZ()-loc2.getBlockZ()); z++){
                    Location locInPath = new Location(loc1.getWorld(),lowX+x, lowY+y, lowZ+z);
                    Block inPath = locInPath.getBlock();
                    blocks.add(inPath.getType());
                }
            }
        }

        for (Material block : blocks) {
            if (!block.equals(Material.AIR) && !block.equals(Material.BARRIER) && !block.equals(Material.LEAVES) && !block.equals(Material.LEAVES_2))
                return true;
        }

        return false;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Prestige to level 75!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "I'M A FIRIN MAH LAZER!",
                "",
                ChatColor.GRAY + "Fires a laser at the nearest enemy.",
                ChatColor.GRAY + "Right clicking slows players around you."
        );
    }
}
