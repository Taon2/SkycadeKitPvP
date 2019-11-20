package net.skycade.kitpvp.kit.kits;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitKnight extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private int steedCooldown = 60;
    private Map<UUID, Horse> horses = new HashMap<>();

    public KitKnight(KitManager kitManager) {
        super(kitManager, "Knight", KitType.KNIGHT, 26000, getLore());

        helmet = new ItemBuilder(
                Material.CHAINMAIL_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Being in a gang with a player using Kit King increases your defence.").build();
        chestplate = new ItemBuilder(
                Material.CHAINMAIL_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Being in a gang with a player using Kit King increases your defence.").build();
        leggings = new ItemBuilder(
                Material.CHAINMAIL_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Being in a gang with a player using Kit King increases your defence.").build();
        boots = new ItemBuilder(
                Material.CHAINMAIL_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Being in a gang with a player using Kit King increases your defence.").build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking every " + steedCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "lets you charge a horse into battle.").build();

        ItemStack icon = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @Override
    public void onMove(Player p) {
        Gang gang = GangsPlusApi.getPlayersGang(p);

        Set<Player> nearby = UtilPlayer.getNearbyPlayers(p.getLocation(), 8);

        gang.getOnlineMembers().forEach(member -> {
            if (nearby.contains(member)) {
                KitPvPStats stats = KitPvP.getInstance().getStats(member);

                if (stats.getActiveKit() == KitType.KING)
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 1));
            }
        });
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.DIAMOND_SWORD)
            return;
        if (!addCooldown(p, "Summon Steed", steedCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        Horse horse = (Horse) p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.HORSE);
        horse.setAdult();
        horse.setPassenger(p);
        horse.setOwner(p);
        horse.setTamed(true);
        horse.setStyle(Horse.Style.WHITE);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING));
        horse.setHealth(horse.getMaxHealth());

        horses.put(p.getUniqueId(), horse);
    }

    @EventHandler
    public void onExit(VehicleExitEvent event) {
        if (horses.containsKey(event.getExited().getUniqueId())) {
            horses.get(event.getExited().getUniqueId()).remove();
            horses.remove(event.getExited().getUniqueId());
        }
    }

    @Override
    public boolean onDeath(Player p) {
        if (horses.containsKey(p.getUniqueId())) {
            p.getVehicle().eject();
            horses.get(p.getUniqueId()).remove();
            horses.remove(p.getUniqueId());
        }

        return true;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (horses.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().getVehicle().eject();
            horses.get(event.getPlayer().getUniqueId()).remove();
            horses.remove(event.getPlayer().getUniqueId());
        }
    }

    public void removeSummon(int seconds, Player p) {
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> {
            if (horses.containsKey(p.getUniqueId())) {
                p.getVehicle().eject();
                horses.get(p.getUniqueId()).remove();
                horses.remove(p.getUniqueId());
            }
        }, seconds * 20);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.AQUA + "" + ChatColor.BOLD + "Defensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Protects his king.",
                "",
                ChatColor.GRAY + "Being in a gang with a player using",
                ChatColor.GRAY + "Kit King nearby increases your defence.",
                ChatColor.GRAY + "Right clicking with your sword mounts you",
                ChatColor.GRAY + "onto a horse."
        );
    }
}
