package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.bukkitevents.KitPvPSpecialAbilityEvent;
import net.skycade.kitpvp.coreclasses.utils.ItemBuilder;
import net.skycade.kitpvp.coreclasses.utils.UtilMath;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static java.lang.Integer.parseInt;
import static net.skycade.kitpvp.Messages.CANNOT_USE;

public class KitZeus extends Kit {

    public KitZeus(KitManager kitManager) {
        super(kitManager, "Zeus", KitType.ZEUS, 30000, "Lightning strikes!");

        Map<String, Object> defaultsMap = new HashMap<>();

        defaultsMap.put("kit.icon.material", "BLAZE_ROD");
        defaultsMap.put("kit.icon.color", "BLACK");
        defaultsMap.put("kit.price", 30000);

        defaultsMap.put("inventory.blaze-rod.enchantments.damage-all", 5);

        defaultsMap.put("armor.material", "LEATHER");
        defaultsMap.put("armor.enchantments.durability", 12);
        defaultsMap.put("armor.enchantments.protection", 4);

        defaultsMap.put("potions.pot1", "FIRE_RESISTANCE:0");

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
    public void applyKit(Player p) {
        p.getInventory().addItem(new ItemBuilder(
                Material.BLAZE_ROD)
                .addEnchantment(Enchantment.DAMAGE_ALL, getConfig().getInt("inventory.blaze-rod.enchantments.damage-all")).build());

        p.getInventory().setArmorContents(getArmour(
                Material.getMaterial(getConfig().getString("armor.material").toUpperCase() + "_HELMET"),
                getConfig().getInt("armor.enchantments.durability"),
                getConfig().getInt("armor.enchantments.protection"),
                Color.fromBGR(153, 255, 255)));

        String[] pot1 = getConfig().getString("potions.pot1").split(":");
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.getByName(pot1[0]),
                Integer.MAX_VALUE,
                parseInt(pot1[1])));
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
            for(Block b : playerBlockList.get(damagee)){
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
    public List<String> getAbilityDesc() {
        return Arrays.asList("§7There is a chance to strike lightning", "§7when you hit someone");
    }

}
