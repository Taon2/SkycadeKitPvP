package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KitAvatar extends Kit {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack abilityItem;

    private int abilityCooldown = 18;
    private double dashYVelocity = 0.07;
    private ArrayList<UUID> flyingParticles = new ArrayList<>();
    private ArrayList<UUID> flyingParticleStay = new ArrayList<>();

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitAvatar(KitManager kitManager) {
        super(kitManager, "Avatar", KitType.AVATAR, 30000, getLore());

        helmet = new ItemBuilder(
                Material.IRON_HELMET).addEnchantment(Enchantment.DURABILITY, 2)
                .build();

        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE).addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .build();

        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS).addEnchantment(Enchantment.DURABILITY, 8)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .build();

        boots = new ItemBuilder(
                Material.IRON_BOOTS).addEnchantment(Enchantment.DURABILITY, 2)
                .build();

        weapon = new ItemBuilder(
                Material.DIAMOND_SWORD).addEnchantment(Enchantment.DURABILITY, 4)
                .build();

        abilityItem = new ItemStack(Material.INK_SACK, 1, (short) 6);
        ItemMeta abilityMeta = abilityItem.getItemMeta();
        abilityMeta.setDisplayName(ChatColor.DARK_AQUA + "Dash");
        List<String> abilityLore = new ArrayList<>();
        abilityLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "%click% to Dash!");
        abilityLore.add(" ");
        abilityLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Dashing will launch you forward towards your enemies");
        abilityLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "and grants you Speed 3 for 6 seconds.");

        abilityMeta.setLore(abilityLore);
        abilityItem.setItemMeta(abilityMeta);

        constantEffects.put(PotionEffectType.SPEED, 0);

        ItemStack icon = new ItemStack(Material.INK_SACK, 1, (short) 6);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().addItem(abilityItem);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "The last Air Bender?",
                "",
                ChatColor.GRAY + "Use your avatar powers to launch yourself",
                ChatColor.GRAY + "forward to catch up to your enemies!"
        );
    }


    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.INK_SACK)
            return;
        if (!item.getItemMeta().hasDisplayName()) // Checks if the ability item doesnt have a display name
            return;
        if (!item.getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Dash")) // Checks if the ability item's name is not "Dash"
            return;

        if (!addCooldown(p, "Dash", abilityCooldown, true))
            return;

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 2.0F, 1.0F);
        p.setVelocity(new org.bukkit.util.Vector(p.getLocation().getDirection().getX(), 0.25, p.getLocation().getDirection().getZ()).multiply(3));

        p.removePotionEffect(PotionEffectType.SPEED);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2));

        flyingParticles.add(p.getUniqueId());
        flyingParticleStay.add(p.getUniqueId());
        new BukkitRunnable() {

            @Override
            public void run() {
                flyingParticleStay.remove(p.getUniqueId());
            }
        }.runTaskLater(KitPvP.getInstance(), 15);

        new BukkitRunnable() {

            @Override
            public void run() {
                constantEffects.forEach((effect, amplifier) -> {
                    p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
                });
            }
        }.runTaskLater(KitPvP.getInstance(), 20 * 6 + 1);
    }

    @Override
    public void onMove(Player p) {
        Location loc = p.getLocation();
        if (loc.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            if (flyingParticles.contains(p.getUniqueId())) {
                if (!flyingParticleStay.contains(p.getUniqueId())) {
                    flyingParticles.remove(p.getUniqueId());
                }
            }
        }
        if (flyingParticles.contains(p.getUniqueId())) {
            if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 5, 5);
            }
        }
    }

}
