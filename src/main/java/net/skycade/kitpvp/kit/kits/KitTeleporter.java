package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitTeleporter extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack pearls;

    private int pearlRegenSpeed = 20;
    private int pearlStartAmount = 5;
    private int pearlMaxAmount = 8;

    private final List<Player> enderpearlCooldown = new ArrayList<>();

    public KitTeleporter(KitManager kitManager) {
        super(kitManager, "Teleporter", KitType.TELEPORTER, 32000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET).build();
        chestplate = new ItemBuilder(
                Material.CHAINMAIL_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        leggings = new ItemBuilder(
                Material.CHAINMAIL_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 7)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build();
        boots = new ItemBuilder(
                Material.IRON_BOOTS).build();
        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD)
                .addEnchantment(Enchantment.DURABILITY, 5).build();
        pearls = new ItemBuilder(
                Material.ENDER_PEARL, pearlStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Regain 1 ender pearl every " + pearlRegenSpeed + " seconds.").build();

        ItemStack icon = new ItemStack(Material.ENDER_PORTAL_FRAME);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(pearls);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, pearlRegenSpeed, new ItemBuilder(
                Material.ENDER_PEARL).build(), pearlMaxAmount, KitType.TELEPORTER);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(player.getItemInHand().getType().equals(Material.ENDER_PEARL) && !addCooldown(e.getPlayer(), getName(), 10, true) || enderpearlCooldown.contains(e.getPlayer())) {
                e.setCancelled(true);
            }

            //For missions
            KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(player, this.getKitType());
            Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

            enderpearlCooldown.add(e.getPlayer());
            Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), () -> enderpearlCooldown.remove(e.getPlayer()), 10);
        }
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Poof!",
                "",
                ChatColor.GRAY + "Has ender pearls to teleport around."
        );
    }
}
