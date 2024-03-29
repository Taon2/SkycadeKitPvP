package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static net.skycade.kitpvp.Messages.CANNOT_USE;

public class KitZeus extends Kit {

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack weapon;

    private Map<PotionEffectType, Integer> constantEffects = new HashMap<>();

    public KitZeus(KitManager kitManager) {
        super(kitManager, "Zeus", KitType.ZEUS, 0, getLore());

        helmet = new ItemBuilder(
                Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(255, 255, 153)).build();
        chestplate = new ItemBuilder(
                Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(255, 255, 153)).build();
        leggings = new ItemBuilder(
                Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(255, 255, 153)).build();
        boots = new ItemBuilder(
                Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.DURABILITY, 12)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .setColour(Color.fromRGB(255, 255, 153)).build();
        weapon = new ItemBuilder(
                Material.BLAZE_ROD)
                .addEnchantment(Enchantment.DAMAGE_ALL, 5)
                .addLore(ChatColor.GRAY + "" + ChatColor.ITALIC + "Damaging players has a chance to smite them with lightning.").build();

        constantEffects.put(PotionEffectType.FIRE_RESISTANCE, 0);

        ItemStack icon = new ItemStack(Material.BLAZE_ROD);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        p.getInventory().addItem(weapon);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);

        constantEffects.forEach((effect, amplifier) -> {
            p.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier));
        });
    }

    private static List<Material> allowedTypes;

    static {
        allowedTypes = Arrays.asList(
                Material.AIR,
                Material.BARRIER,
                Material.LEAVES,
                Material.LEAVES_2
        );
    }

    @Override
    public void onDamageDealHit(EntityDamageByEntityEvent e, Player damager, Player damagee) {
        if (UtilMath.getRandom(0, 100) <= 7) {
            HashMap<Player, List<Block>> playerBlockList = getBlocks(damagee);
            for (Block b : playerBlockList.get(damagee)){
                if (!allowedTypes.contains(b.getType())) {
                    CANNOT_USE.msg(damager, "%thing%", "lightning", "%reason%", "while under a block!");
                    return;
                }
            }

            //For missions
            KitPvPSpecialAbilityEvent abilityEvent = new KitPvPSpecialAbilityEvent(damager, this.getKitType());
            Bukkit.getServer().getPluginManager().callEvent(abilityEvent);

            damagee.getWorld().strikeLightning(damagee.getLocation());
            e.setDamage(e.getDamage() * 1.4);
            damagee.setFireTicks(60);
        }
    }

    private static HashMap<Player, List<Block>> getBlocks(Player player){
        Block block = player.getLocation().getBlock();
        HashMap<Player, List<Block>> playerBlockList = new HashMap<>();
        playerBlockList.put(player, new ArrayList<>());
        Location location = block.getLocation();

        for(int y = 0; y < 256; y++){
            location.setY(block.getY() + 1);
            block = location.getBlock();
            playerBlockList.get(player).add(block);
        }

        return playerBlockList;
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /eventshop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.RED + "" + ChatColor.BOLD + "Offensive Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Thou hast been smitten!",
                "",
                ChatColor.GRAY + "Strikes lightning on your foes."
        );
    }
}
