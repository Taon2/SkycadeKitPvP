package net.skycade.kitpvp.kit;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.commands.*;
import net.skycade.kitpvp.commands.staff.*;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.kit.kits.disabled.*;
import org.bukkit.Bukkit;

import java.util.*;

public class KitManager {

    private final KitPvP plugin;

    private final Map<KitType, Kit> kits = new LinkedHashMap<>();
    private final Map<UUID, Integer> signRefreshCooldown = new HashMap<>();

    public KitManager(KitPvP plugin) {
        this.plugin = plugin;

        registerKits();
        startSignMapUpdate();

        new CommandEventShop();
        new CommandKit();
        new CommandKitName();
        new CommandKitPvPHelp();
        new CommandPrestige();
        new CommandRefreshKit();
        new CommandShop();
        new CommandSoup();
        new CommandViewKit();
        new CommandViewStats();

        //Staff commands
        new CommandEco();
        new CommandEventEco();
        new CommandKitsUnlocked();
        new CommandRefund();
        new CommandResetStats();
        new CommandSetStats();
        new CommandTriggerEvent();
    }

    private void registerKits() {
        registerKit(new KitArcher(this));
        registerKit(new KitAssassin(this));
        registerKit(new KitBarbarian(this));
        registerKit(new KitBlacksmith(this));
        registerKit(new KitBladeMaster(this));
        registerKit(new KitBlockhunt(this));
        registerKit(new KitBomber(this));
        registerKit(new KitBuildUHC(this));
        registerKit(new KitCaveMan(this));
        registerKit(new KitCerberus(this));
        registerKit(new KitChance(this));
        registerKit(new KitCobra(this));
        registerKit(new KitDualBlader(this));
        registerKit(new KitDubstep(this));
        registerKit(new KitElite(this));
        registerKit(new KitEnderman(this));
        registerKit(new KitFisherman(this));
        registerKit(new KitFrosty(this));
        registerKit(new KitGambler(this));
        registerKit(new KitGank(this));
        registerKit(new KitGuardian(this));
        registerKit(new KitHades(this));
        registerKit(new KitHulk(this));
        registerKit(new KitHydra(this));
        registerKit(new KitHyper(this));
        registerKit(new KitJesus(this));
        registerKit(new KitJumper(this));
        registerKit(new KitKangaroo(this));
        registerKit(new KitKing(this));
        registerKit(new KitKnight(this));
        registerKit(new KitLich(this));
        registerKit(new KitMultishot(this));
        registerKit(new KitMystic(this));
        registerKit(new KitNecromancer(this));
        registerKit(new KitPaladin(this));
        registerKit(new KitPlush(this));
        registerKit(new KitPotionMaster(this));
        registerKit(new KitPrick(this));
        registerKit(new KitPyromancer(this));
        registerKit(new KitShaco(this));
        registerKit(new KitSharingan(this));
        registerKit(new KitShroom(this));
        registerKit(new KitSniper(this));
        registerKit(new KitSonic(this));
        registerKit(new KitSoulMaster(this));
        registerKit(new KitTank(this));
        registerKit(new KitTeleporter(this));
        registerKit(new KitTreeEnt(this));
        registerKit(new KitWarrior(this));
        registerKit(new KitWitchdoctor(this));
        registerKit(new KitWither(this));
        registerKit(new KitWolfPack(this));
        registerKit(new KitZeus(this));
        registerKit(new KitMaster(this));

        //Disabled kits, disabled because they were lame and new ones replaced them
        registerKit(new KitChronos(this));
        registerKit(new KitDefault(this));
        registerKit(new KitFireArcher(this));
        registerKit(new KitFireMage(this));
        registerKit(new KitGolem(this));
        registerKit(new KitGhost(this));
        registerKit(new KitHuntsman(this));
        registerKit(new KitLover(this));
        registerKit(new KitMedic(this));
        registerKit(new KitNinja(this));
        registerKit(new KitStrafe(this));
        registerKit(new KitSupport(this));
        registerKit(new KitTribesman(this));
        registerKit(new KitUnicorn(this));
        registerKit(new KitVampire(this));
        registerKit(new KitWizard(this));
    }

    private void registerKit(Kit kit) {
        kits.put(kit.getKitType(), kit);
        Bukkit.getPluginManager().registerEvents(kit, plugin);
    }

    private void startSignMapUpdate() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            List<UUID> removeFromEntry = new ArrayList<>();
            signRefreshCooldown.forEach((key, value) -> {
                int cd = value;
                cd--;
                if (cd <= 0) {
                    removeFromEntry.add(key);
                }
                signRefreshCooldown.replace(key, cd);
            });
            removeFromEntry.forEach(signRefreshCooldown::remove);
        }, 20, 20);
    }

    public Map<KitType, Kit> getKits() {
        return kits;
    }

    public Map<UUID, Integer> getSignMap() {
        return signRefreshCooldown;
    }

    public KitPvP getKitPvP() {
        if (plugin == null)
            return KitPvP.getInstance();
        return plugin;
    }

}