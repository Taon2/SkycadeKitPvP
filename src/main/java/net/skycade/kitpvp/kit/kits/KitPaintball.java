package net.skycade.kitpvp.kit.kits;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import net.skycade.SkycadeCore.vanish.VanishStatus;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.Messages;
import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitPaintball extends Kit {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;
    private ItemStack paintballs;

    private int maxPaintBalls = 8;

    private int regenerationSpeed = 4;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitPaintball(KitManager kitManager) {
        super(kitManager, "Paintball", KitType.PAINTBALL, 26000, getLore());

        helmet = new ItemBuilder(Material.IRON_HELMET)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.DURABILITY, 4)
                .build();

        chestplate = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.DURABILITY, 15)
                .setColour(Color.PURPLE)
                .build();

        leggings = new ItemBuilder(Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .addEnchantment(Enchantment.DURABILITY, 15)
                .setColour(Color.PURPLE)
                .build();

        boots = new ItemBuilder(Material.IRON_BOOTS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .addEnchantment(Enchantment.DURABILITY, 4)
                .build();

        weapon = new ItemBuilder(Material.DIAMOND_HOE)
                .setName(ChatColor.GREEN + "Paintball Gun")
                .addEnchantment(Enchantment.DAMAGE_ALL, 4)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Right-Click to fire your Paintball Gun!")
                .build();

        paintballs = new ItemStack(Material.SNOW_BALL);
        ItemMeta paintballMeta = paintballs.getItemMeta();
        paintballMeta.setDisplayName(ChatColor.YELLOW + "Paintball Ammunition");
        List<String> painballLore = new ArrayList<>();
        painballLore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Ammunition regenerates every " + regenerationSpeed + " seconds");

        paintballMeta.setLore(painballLore);
        paintballs.setItemMeta(paintballMeta);

        constantEffects.put(PotionEffectType.SPEED, 0);

        ItemStack icon = new ItemStack(Material.DIAMOND_HOE);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setItem(27, paintballs);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });

        startItemRunnable(p, regenerationSpeed, getPaintballs(1), maxPaintBalls, KitType.PAINTBALL);
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.GOLD + "" + ChatColor.BOLD + "Ranged Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Did someone say guns?!",
                "",
                ChatColor.GRAY + "Shoot paintballs at your enemies to do",
                ChatColor.GRAY + "damage from a long distance!"
        );
    }

    private ItemStack getPaintballs(int amount) {
        ItemStack regenPaintball = new ItemStack(paintballs);
        regenPaintball.setAmount(amount);

        return regenPaintball;
    }

    @Override
    public void onItemUse(Player p, ItemStack item) {
        if (item.getType() != Material.DIAMOND_HOE)
            return;
        if (KitPvP.getInstance().getEventManager().getLMS().isPlaying(p) &&
                !KitPvP.getInstance().getEventManager().getLMS().isFighting() ||
                KitPvP.getInstance().getEventManager().getLMS().isSpectating(p))
            return;
        if (p.getGameMode() == GameMode.CREATIVE || VanishStatus.isVanished(p.getUniqueId())) {
            p.launchProjectile(Snowball.class);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
            return;
        }
        if (!p.getInventory().contains(Material.SNOW_BALL)) {
            ActionBarAPI.sendActionBar(p, ChatColor.translateAlternateColorCodes('&', Messages.OUT_OF_AMMUNITION.getMessage()));
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
            return;
        }

        //For missions
        KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(p, this.getKitType());
        Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

        p.getInventory().removeItem(paintballs);
        p.updateInventory();
        p.launchProjectile(Snowball.class);
        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
    }

    @EventHandler
    public void ammunitionLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Snowball) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player player = (Player) event.getEntity().getShooter();
                Kit kit = KitPvP.getInstance().getStats(player).getActiveKit().getKit();

                if (kit.getKitType() == KitType.PAINTBALL) {
                    if (player.getInventory().getItemInHand().getType() == Material.SNOW_BALL) {
                        event.setCancelled(true);
                        player.getInventory().addItem(paintballs);
                        player.updateInventory();

                        ActionBarAPI.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', Messages.CANNOT_THROW_AMMUNITION.getMessage()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void paintballHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Snowball)) return;
        if (!(((Snowball) event.getDamager()).getShooter() instanceof Player)) return;

        Player shooter = (Player) ((Snowball) event.getDamager()).getShooter();
        Player victim = (Player) event.getEntity();

        Kit kit = KitPvP.getInstance().getStats(shooter).getActiveKit().getKit();
        Kit victimkit = KitPvP.getInstance().getStats(victim).getActiveKit().getKit();
        if (victimkit.getKitType() == KitType.ENDERMAN) return;
        if (kit.getKitType() == KitType.PAINTBALL) {
            // This is for making sure it sets the player in combat
            victim.damage(1, shooter);
            // If player is below 2 hearts or whatever
            if (victim.getHealth() <= 4){
                // victim.setHealth(0D) will make errors so I just made it deal 100 damage to instant kill them
                victim.damage(100, shooter);
                return;
            }
            // Removes like 2 hearts from the player (ignoring armor)
            victim.setHealth(victim.getHealth() - 4D);
        }
    }
}
