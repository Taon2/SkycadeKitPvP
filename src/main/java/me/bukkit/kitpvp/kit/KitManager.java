package me.bukkit.kitpvp.kit;

import me.bukkit.kitpvp.KitPvP;
import me.bukkit.kitpvp.commands.*;
import me.bukkit.kitpvp.commands.staff.*;
import me.bukkit.kitpvp.coreclasses.commands.Command;
import me.bukkit.kitpvp.coreclasses.commands.CommandManager;
import me.bukkit.kitpvp.coreclasses.commands.Module;
import me.bukkit.kitpvp.coreclasses.region.DataPoint;
import me.bukkit.kitpvp.coreclasses.region.Region;
import me.bukkit.kitpvp.duel.Duel;
import me.bukkit.kitpvp.kit.kits.*;
import me.bukkit.kitpvp.listeners.SignListeners;
import me.bukkit.kitpvp.ui.KitsMenu;
import me.bukkit.kitpvp.ui.AchievementsMenu;
import me.bukkit.kitpvp.ui.ShopMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class KitManager extends Module {

	private final KitPvP plugin;

	private final List<UUID> spawnList = new ArrayList<>();
	private final List<Command> commands = new ArrayList<>();

	private final Map<KitType, Kit> kits = new LinkedHashMap<>();
	private final Map<UUID, Integer> signRefreshCooldown = new HashMap<>();
    private final Map<UUID, UUID> playerDuel = new HashMap<>();
    private final Map<UUID, Kit> duelKit = new HashMap<>();
    private final Map<UUID, UUID> lastPlayerFought = new HashMap<>();

    //To check if moving

    private final World world = getKitPvP().getWorld();

    //TODO add 4 duel arena's
    private final Duel[] duels = {
        new Duel(new Region("arena1", new DataPoint(0, 70, 0), new DataPoint(0, 70, 0)), new Location(world, 1273.5, 17, 76.4, -130, 0), new Location(world, 1333.4, 17, 16.5, 40, 0)),
        new Duel(new Region("arena2", new DataPoint(0, 70, 0), new DataPoint(0, 70, 0)), new Location(world, 1339.4, 17, 75.5, -130, 0), new Location(world, 1399.7, 17, 15.7, 40, 0)),
        new Duel(new Region("arena3", new DataPoint(0, 70, 0), new DataPoint(0, 70, 0)), new Location(world, 1338.5, 17, 140, -130, 0), new Location(world, 1398.5, 17, 80, 40, 0)),
        new Duel(new Region("arena4", new DataPoint(0, 70, 0), new DataPoint(0, 70, 0)), new Location(world,  1273.5, 17, 140.5, -130, 0), new Location(world, 1334.4, 17, 80.5, 40, 0))
    };
	
	private final KitsMenu kitsMenu;
	private final ShopMenu shopMenu;
	private final AchievementsMenu achievementsMenu;
	
	public KitManager(KitPvP plugin) {
		this.plugin = plugin;

		registerKits();
        startSignMapUpdate();

		kitsMenu = new KitsMenu(this);
		shopMenu = new ShopMenu(this);
		achievementsMenu = new AchievementsMenu(this);
		CommandCrate commandCrate = new CommandCrate(this);
		CommandViewKit commandViewKit = new CommandViewKit(this);

		registerCommand(commandViewKit);
		registerCommand(new CommandAchievements(this));
		registerCommand(commandCrate);
		registerCommand(new CommandEco(this));
		registerCommand(new CommandKit(this));
		registerCommand(new CommandKitName(this));
		registerCommand(new CommandKitPvPHelp(this));
		registerCommand(new CommandKitpvpStats(this));
		registerCommand(new CommandResetStats(this));
		registerCommand(new CommandShop(this));
		registerCommand(new CommandSoup(this));
		registerCommand(new CommandTeam(this));
		registerCommand(new CommandUpgrade(this));
		registerCommand(new CommandViewKit(this));
		registerCommand(new CommandUnlock(this));
		registerCommand(new CommandSetLevel(this));
		registerCommand(new CommandSpawn(this));
		registerCommand(new CommandSetStats(this));
		registerCommand(new CommandTopStats(this));
		registerCommand(new CommandUnlock(this));
		registerCommand(new CommandGiveKeys(this));
		registerCommand(new CommandViewStats(this));
		registerCommand(new CommandKitsUnlocked(this));
		registerCommand(new CommandTp(this));
		registerCommand(new CommandDuel(this));
		registerCommand(new RefundCommand(this));

		registerListener(achievementsMenu);
        registerListener(shopMenu);
        registerListener(kitsMenu);
        registerListener(commandViewKit);
		registerListener(commandCrate);
		registerListener(new SignListeners(this));
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
		registerKit(new KitChronos(this));
		registerKit(new KitCobra(this));
		registerKit(new KitDualBlader(this));
		registerKit(new KitDubstep(this));
		registerKit(new KitElite(this));
		registerKit(new KitEnderman(this));
		registerKit(new KitFireArcher(this));
        registerKit(new KitFireMage(this));
		registerKit(new KitFisherman(this));
		registerKit(new KitFrosty(this));
		registerKit(new KitGank(this));
		registerKit(new KitGhost(this));
		registerKit(new KitGolem(this)); //kit is disabled.
		registerKit(new KitHades(this));
		registerKit(new KitHuntsman(this));
		registerKit(new KitHydra(this));
		registerKit(new KitHyper(this));
		registerKit(new KitJesus(this));
		registerKit(new KitJumper(this));
		registerKit(new KitKangaroo(this));
		registerKit(new KitKing(this));
		registerKit(new KitKnight(this));
		registerKit(new KitLover(this));
		registerKit(new KitMedic(this));
		registerKit(new KitMystic(this));
		registerKit(new KitNinja(this));
		registerKit(new KitPlush(this));
		registerKit(new KitPotionMaster(this));
		registerKit(new KitPrick(this));
		registerKit(new KitShaco(this));
		registerKit(new KitSharingan(this));
		registerKit(new KitSniper(this));
		registerKit(new KitSonic(this));
		registerKit(new KitSoulMaster(this));
		registerKit(new KitStrafe(this));
		registerKit(new KitSupport(this));
		registerKit(new KitTank(this));
		registerKit(new KitTeleporter(this));
		registerKit(new KitTribesman(this));
		registerKit(new KitUnicorn(this));
		registerKit(new KitVampire(this));
		registerKit(new KitWarrior(this));
		registerKit(new KitWither(this));
		registerKit(new KitWizard(this));
		registerKit(new KitWolfPack(this));
		registerKit(new KitZeus(this));
		registerKit(new KitMaster(this));
	}

	private void registerKit(Kit kit) {
		kits.put(kit.getKitType(), kit);
		Bukkit.getPluginManager().registerEvents(kit, plugin);
	}

	private void startSignMapUpdate() {
	    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
	        List<UUID> removeFromEntry = new ArrayList<>();
	        signRefreshCooldown.entrySet().forEach(entry -> {
	            UUID key = entry.getKey();
	            int cd = entry.getValue();
	            cd--;
	            if (cd <= 0) {
	                removeFromEntry.add(key);
                }
                signRefreshCooldown.replace(key, cd);
            });
	        removeFromEntry.forEach(signRefreshCooldown::remove);
        }, 20, 20);
    }

    public List<UUID> getSpawnList() {
        return spawnList;
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
	
	public Map<UUID, UUID> getPlayerDuel() {
        return playerDuel;
    }
    
    public Map<UUID, Kit> getDuelKit() {
        return duelKit;
    }
    
    public Map<UUID, UUID> getLastPlayerFought() {
        return lastPlayerFought;
    }
    
    public Duel[] getDuels() {
        return duels;
    }

    public KitsMenu getMenu() {
        return kitsMenu;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    public AchievementsMenu getAchievementsMenu() {
        return achievementsMenu;
    }

	public KitPvP getKitPvP() {
		if (plugin == null)
			return KitPvP.getInstance();
		return plugin;
	}

}