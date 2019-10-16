package net.skycade.kitpvp.kit;

import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.commands.*;
import net.skycade.kitpvp.commands.staff.*;
import net.skycade.kitpvp.coreclasses.commands.Command;
import net.skycade.kitpvp.coreclasses.commands.CommandManager;
import net.skycade.kitpvp.coreclasses.commands.Module;
import net.skycade.kitpvp.kit.kits.*;
import net.skycade.kitpvp.kit.kits.disabled.*;
import net.skycade.kitpvp.ui.KitsMenu;
import net.skycade.kitpvp.ui.ShopMenu;
import org.bukkit.Bukkit;

import java.util.*;

public class KitManager extends Module {

    private final KitPvP plugin;

    private final List<Command> commands = new ArrayList<>();

    private final Map<KitType, Kit> kits = new LinkedHashMap<>();
    private final Map<UUID, Integer> signRefreshCooldown = new HashMap<>();

    private final KitsMenu kitsMenu;
    private final ShopMenu shopMenu;

    public KitManager(KitPvP plugin) {
        this.plugin = plugin;

        registerKits();
        startSignMapUpdate();

        kitsMenu = new KitsMenu(this);
        shopMenu = new ShopMenu(this);
        CommandCrate commandCrate = new CommandCrate(this);
        CommandViewKit commandViewKit = new CommandViewKit(this);

        registerCommand(commandViewKit);
        registerCommand(commandCrate);
        registerCommand(new CommandEco(this));
        registerCommand(new CommandKit(this));
        registerCommand(new CommandKitName(this));
        registerCommand(new CommandKitPvPHelp(this));
        registerCommand(new CommandResetStats(this));
        registerCommand(new CommandShop(this));
        registerCommand(new CommandSoup(this));
        registerCommand(new CommandRefreshKit(this));
        registerCommand(new CommandViewKit(this));
        registerCommand(new CommandUnlock(this));
        registerCommand(new CommandSetStats(this));
        registerCommand(new CommandTopStats(this));
        registerCommand(new CommandUnlock(this));
        registerCommand(new CommandViewStats(this));
        registerCommand(new CommandKitsUnlocked(this));
        registerCommand(new RefundCommand(this));
        registerCommand(new CommandReload(this));
        registerCommand(new TriggerEventCommand(this));

        registerListener(shopMenu);
        registerListener(kitsMenu);
        registerListener(commandViewKit);
        registerListener(commandCrate);
    }

    @Override
    public void registerCommand(Command<? extends Module> command) {
        commands.add(command);
        CommandManager.getInstance().registerCommand(command);
    }

    private void registerKits() {
        registerKit(new KitDefault(this));
        registerKit(new KitArcher(this));
        registerKit(new KitAssassin(this));
        registerKit(new KitBarbarian(this));
        registerKit(new KitBladeMaster(this));
        registerKit(new KitBomber(this));
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
        registerKit(new KitGank(this));
        registerKit(new KitHades(this));
        registerKit(new KitHydra(this));
        registerKit(new KitHyper(this));
        registerKit(new KitJesus(this));
        registerKit(new KitJumper(this));
        registerKit(new KitKangaroo(this));
        registerKit(new KitKing(this));
        registerKit(new KitKnight(this));
        registerKit(new KitMystic(this));
        registerKit(new KitPlush(this));
        registerKit(new KitPotionMaster(this));
        registerKit(new KitPrick(this));
        registerKit(new KitShaco(this));
        registerKit(new KitSharingan(this));
        registerKit(new KitSniper(this));
        registerKit(new KitSonic(this));
        registerKit(new KitSoulMaster(this));
        registerKit(new KitTank(this));
        registerKit(new KitTeleporter(this));
        registerKit(new KitWarrior(this));
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

    public List<Command> getCommands() {
        return commands;
    }

    public Map<KitType, Kit> getKits() {
        return kits;
    }

    public Map<UUID, Integer> getSignMap() {
        return signRefreshCooldown;
    }

    public KitsMenu getMenu() {
        return kitsMenu;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    public KitPvP getKitPvP() {
        if (plugin == null)
            return KitPvP.getInstance();
        return plugin;
    }

}