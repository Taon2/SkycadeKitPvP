package net.skycade.kitpvp.kit.kits.disabled;

import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.ParticleEffect;
import net.skycade.kitpvp.coreclasses.utils.UtilPlayer;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.HEALED;
import static net.skycade.kitpvp.Messages.PLAYER_HEALED;

public class KitMedic extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack shears;
    private ItemStack medpack;

    private int medpackRegenSpeed = 8;
    private int medpackStartAmount = 5;
    private int medpackMaxAmount = 5;

    private int shearsCooldown = 4;

    public KitMedic(KitManager kitManager) {
        super(kitManager, "Medic", KitType.MEDIC, 16000, false, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .setColour(Color.RED).build();
        weapon = new ItemBuilder(
                Material.IRON_HOE)
                .addEnchantment(Enchantment.DURABILITY, 5)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5).build();
        shears = new ItemBuilder(
                Material.SHEARS)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right clicking a player every " + shearsCooldown + " seconds")
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "heals them.").build();
        medpack = new ItemBuilder(
                Material.LEATHER, medpackStartAmount)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Drop to heal players around you.")
                .setName("Medpack").build();

        ItemStack icon = new ItemStack(Material.SHEARS);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(shears);
        p.getInventory().addItem(medpack);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        startItemRunnable(p, medpackRegenSpeed, getMedpack(1), medpackMaxAmount, KitType.MEDIC);
    }

    public void onMedpackUse(Player p, Item medpack) {
        Set<Player> players = UtilPlayer.getNearbyPlayers(p.getLocation(), 3);
        players.remove(p);

        players.forEach((player) -> {
            player.setHealth(player.getMaxHealth());
            HEALED.msg(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
            PLAYER_HEALED.msg(p, "%player%", player.getName());
        });
        Bukkit.getScheduler().runTaskLater(getKitManager().getKitPvP(), medpack::remove, 8);
    }

    public void onMove(Player p) {
        if (p.getItemInHand().getType().equals(Material.LEATHER)) {
            particleMoveEffect(p, ParticleEffect.CRIT, 3, 16);
        }
    }

    @Override
    public void onInteract(Player p, Player target, ItemStack item) {
        if (item.getType() != Material.SHEARS)
            return;
        if (!addCooldown(p, "Stitch Up", shearsCooldown, true))
            return;
        target.setHealth(target.getMaxHealth());
        PLAYER_HEALED.msg(p, "%player%", target.getName());
        HEALED.msg(target);
    }

    private ItemStack getMedpack(int amount) {
        ItemStack medpackRegen = new ItemStack(medpack);
        medpackRegen.setAmount(amount);

        return medpackRegen;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Unobtainable.");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GREEN + "" + ChatColor.BOLD + "Support Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "I need healing!",
                "",
                ChatColor.GRAY + "Shears heal players you click.",
                ChatColor.GRAY + "Throwing medpacks can heal players."
        );
    }
}
