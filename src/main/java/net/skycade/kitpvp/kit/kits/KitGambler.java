package net.skycade.kitpvp.kit.kits;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.kit.Kit;
import net.skycade.kitpvp.kit.KitManager;
import net.skycade.kitpvp.kit.KitType;
import net.skycade.kitpvp.stat.KitPvPStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.skycade.kitpvp.Messages.KIT_EQUIPPED;

public class KitGambler extends Kit {

    public KitGambler(KitManager kitManager) {
        super(kitManager, "Gambler", KitType.GAMBLER, 45000, getLore());

        ItemStack icon = new ItemStack(Material.TRIPWIRE_HOOK);
        setIcon(icon);
    }

    @Override
    public void applyKit(Player p) {
        KitPvPStats stats = KitPvP.getInstance().getStats(p);

        KitType randomKit = (KitType) stats.getKits().keySet().toArray()[new Random().nextInt(stats.getKits().keySet().toArray().length)];
        if (randomKit == KitType.GAMBLER || !randomKit.getKit().isEnabled())
            applyKit(p);

        getKitManager().getKitPvP().getStats(p).setActiveKit(randomKit);
        getKitManager().getKitPvP().getStats(p).setKitPreference(KitType.GAMBLER);

        randomKit.getKit().beginApplyKit(p);

        KIT_EQUIPPED.msg(p, "%kit%", randomKit.getKit().getName());
    }

    @Override
    public List<String> getHowToObtain() {
        return Collections.singletonList(ChatColor.GRAY + "" + ChatColor.ITALIC + "Purchase from /shop!");
    }

    public static List<String> getLore() {
        return Arrays.asList(
                ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Random Kit",
                ChatColor.GRAY + "" + ChatColor.ITALIC + "Which one will it be?",
                "",
                ChatColor.GRAY + "Applies a random kit you",
                ChatColor.GRAY + "own upon each death."
        );
    }
}
