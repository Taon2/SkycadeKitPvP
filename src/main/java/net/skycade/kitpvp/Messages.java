package net.skycade.kitpvp;

import net.skycade.SkycadeCore.Localization;

public class Messages {

    //KitPvP General Messages
    public static final Localization.Message RELOADED = new Localization.Message("reloaded", "&aReloaded!");
    public static final Localization.Message NOT_LOADED = new Localization.Message("not-loaded", "&cYour data has not loaded yet, please wait...");
    public static final Localization.Message ALL_KITS_UNLOCKED = new Localization.Message("all-kits-unlocked", "&7You unlocked &aall &7the kits! You unlocked the &aKitMaster &7kit.");
    public static final Localization.Message COULDNT_FIND = new Localization.Message("couldnt-find", "&cCould not find &b%type% &c'&b%thing%&c'.");
    public static final Localization.Message ON_COOLDOWN = new Localization.Message("on-cooldown",  "&cYou need to wait another &b%time% &cbefore using &b%thing% &cagain!");
    public static final Localization.Message ON_COOLDOWN_NO_TIME = new Localization.Message("on-cooldown-no-time", "&b%thing% &cis on cooldown!");
    public static final Localization.Message OFF_COOLDOWN = new Localization.Message("off-cooldown", "&7You can now use &a%thing%&7.");
    public static final Localization.Message NOT_ENOUGH = new Localization.Message("not-enough", "&cYou don't have enough &b%thing%&7.");
    public static final Localization.Message CANNOT_USE = new Localization.Message("cannot-use", "&cYou cannot use &b%thing% %reason%&c!");
    public static final Localization.Message ALREADY_USING = new Localization.Message("already-using", "&cYou are already using kit &b%kit%&c.");
    public static final Localization.Message KIT_EQUIPPED = new Localization.Message("kit-equipped", "&7Equipped kit &a%kit%&7.");
    public static final Localization.Message KIT_EQUIPPED_RESPAWN = new Localization.Message("kit-equipped", "&7Equipped kit &a%kit%&7. It will be active after you respawn.");
    public static final Localization.Message YOU_PURCHASED = new Localization.Message("you-purchased", "&7You bought &a%thing% &7for &a%amount% &7%currency%.");
    public static final Localization.Message DIDNT_CHOOSE = new Localization.Message("didnt-choose", "&cYou didn't choose &b%thing%&c.");
    public static final Localization.Message NOT_ENOUGH_CURRENCY = new Localization.Message("not-enough-currency", "&cYou don't have enough &b%currency% &cto purchase this &b%thing%&c.");
    public static final Localization.Message KIT_DISABLED = new Localization.Message("kit-disabled", "&cThat kit is disabled.");
    public static final Localization.Message ALREADY_UNLOCKED = new Localization.Message("already-unlocked", "&cYou already have this &b%kit% &cunlocked.");

    //KitPvP Command Messages
    public static final Localization.Message USING_KIT = new Localization.Message("using-kit", "&a%player%&7 is using the &a%kitname%&7 kit.");
    public static final Localization.Message NO_LAST_KILLSTREAK = new Localization.Message("command.no-last-killstreak", "&cLast killstreak can't be found for %player%.");
    public static final Localization.Message CURRENT_KILlSTREAK_HIGHER = new Localization.Message("command.current-killstreak-higher", "&cCurrent killstreak is higher than last killstreak.");
    public static final Localization.Message KILLSTREAK_REFUNDED = new Localization.Message("command.killstreak-refunded", "&aYour killstreak was refunded.");
    public static final Localization.Message KIT_UNLOCKED = new Localization.Message("command.kit-unlocked", "&a&l%player% now has %kit% unlocked.");
    public static final Localization.Message YOUR_KIT_UNLOCKED = new Localization.Message("command.your-kit-unlocked", "&a%kit% &7was unlocked.");
    public static final Localization.Message KIT_LOCKED = new Localization.Message("command.kit-locked", "&a&l%player% now has %kit% locked.");
    public static final Localization.Message YOUR_KIT_LOCKED = new Localization.Message("command.your-kit-locked", "&a%kit% &7was unlocked.");
    public static final Localization.Message CURRENCY_RESET = new Localization.Message("command.currency-reset", "&7%player%'s %currency% were &creset&7.");
    public static final Localization.Message YOUR_CURRENCY_RESET = new Localization.Message("command.your-currency-reset", "&7Your %currency% were &creset&7.");
    public static final Localization.Message CURRENCY_ADDED = new Localization.Message("command.currency-added", "&a%amount% &7%currency% given to &a%player%&7.");
    public static final Localization.Message YOUR_CURRENCY_ADDED = new Localization.Message("command.your-currency-added", "&7You got &a%amount% &7%currency%, your total balance is now &a%total% &7%currency%.");
    public static final Localization.Message CURRENCY_REMOVED = new Localization.Message("command.currency-removed", "&a%player%'s &7balance was lowered by &a%amount% &7%currency% to &a%total%&7.");
    public static final Localization.Message YOUR_CURRENCY_REMOVED = new Localization.Message("command.your-currency-removed", "&7Your balance was lowered by &a%amount% &7%currency%.");
    public static final Localization.Message STATS_RESET = new Localization.Message("command.stats-reset", "&a%player%'s &7stats are reset.");
    public static final Localization.Message STAT_SET = new Localization.Message("command.stat-set", "%player%'s %stat% has been set to %amount%.");
    public static final Localization.Message YOUR_STAT_SET = new Localization.Message("command.your-stat-set", "&7Your %stat% has been set to %amount%.");

    //KitPvP Stats
    public static final Localization.Message STATS = new Localization.Message("command.your-stat-set",
            "&7---------- &f&l%player%'s KitPvP Stats &7----------\n" +
                    "&fKits - &a%kits%\n" +
                    "&fCoins - &a%coins%\n" +
                    "&fEvent Tokens - &a%eventtokens%\n" +
                    "&fDeaths - &a%deaths%\n" +
                    "&fKills - &a%kills%\n" +
                    "&fAssists - &a%assists%\n" +
                    "&fHighest Killstreak - &a%killstreak%\n"
            );

    //KitPvP Event Messages
    public static final Localization.Message NO_SUCH_EVENT = new Localization.Message("event.no-such-event", "&cNo such event.");

    //Double Credits
    public static final Localization.Message DOUBLECREDITS_START = new Localization.Message("event.doublecredits-start", "&a&lDOUBLE CREDITS! &r&aFor the next 30 minutes, everybody earns double the amount of coins!");
    public static final Localization.Message DOUBLECREDITS_ENDED = new Localization.Message("event.doublecredits-ended", "&a&lDOUBLE CREDITS ENDED!");

    //Kill The King
    public static final Localization.Message KILLTHEKING_START = new Localization.Message("event.killtheking-start", "&a&lKILL THE KING! &r&a%player% is the King. Everyone attack them!");
    public static final Localization.Message KILLTHEKING_TOO_LONG = new Localization.Message("event.killtheking-too-long", "&a&lThe King took too long to kill.");
    public static final Localization.Message KILLTHEKING_PARTICIPATE = new Localization.Message("event.killtheking-participate", "You have received %amount% Event Tokens for participating!");
    public static final Localization.Message KILLTHEKING_WON = new Localization.Message("event.killtheking-won", "You have killed the King! You have received %amount% Event Tokens for winning!");
    public static final Localization.Message KILLTHEKING_KILLED_BY = new Localization.Message("event.killtheking-killed-by", "&c%player% &ahas killed the King!");
    public static final Localization.Message KILLTHEKING_KILLED = new Localization.Message("event.killtheking-killed", "&aThe King has been killed!");
    public static final Localization.Message KILLTHEKING_LOGGED_OUT = new Localization.Message("event.killtheking-logged-out", "&aThe King has been killed!");
    public static final Localization.Message KILLTHEKING_TO_SPAWN = new Localization.Message("event.killtheking-to-spawn", "&aThe King has been killed!");
    public static final Localization.Message KILLTHEKING_ENDED = new Localization.Message("event.killtheking-ended", "&a&lKILL THE KING ENDED!");

    //Tag
    public static final Localization.Message TAG_START = new Localization.Message("event.tag-start", "&a&lINFECTION! &r&a%player% is infected. Stay away from them for 5 minutes to get a coin bonus!");
    public static final Localization.Message TAG_ENDED = new Localization.Message("event.tag-ended", "&a&lINFECTION ENDED!");

    //Team Fight
    public static final Localization.Message TEAMFIGHT_START = new Localization.Message("event.teamfight-start", "&a&lTEAM FIGHT! &r&aFight against the opposite team!");
    public static final Localization.Message TEAMFIGHT_STARTING = new Localization.Message("event.teamfight.starting", "&a&lTEAM FIGHT &astarting in &6%time%&a!");
    public static final Localization.Message TEAMFIGHT_TEAM1 = new Localization.Message("event.teamfight.team1", "&a&lYou have been assigned to team &c&lRED&a!\n" +
            "&aFight against the other team. The team with the most kills gets &6&l2x coins &aat the end for 30 minutes!");
    public static final Localization.Message TEAMFIGHT_TEAM2 = new Localization.Message("event.teamfight.team2", "&a&lYou have been assigned to team &9&lBLUE&a!\n" +
            "&bFight against the other team. The team with the most kills gets &6&l2x coins &bat the end for 30 minutes!");
    public static final Localization.Message TEAMFIGHT_FINAL_STATS = new Localization.Message("event.teamfight.final-stats", "&a&lThe team fight event is over! &aHere are the results:\n" +
            "&bTeam &c&lRED&b: &6%kills1% kill%s1%\n" +
            "&bTeam &9&lBLUE&b: &6%kills2% kill%s2%");
    public static final Localization.Message TEAMFIGHT_DRAW = new Localization.Message("event.teamfight.draw", "&aThe event ended with a &e&lDRAW&a! Thanks to everyone for participating!");
    public static final Localization.Message TEAMFIGHT_WINNER = new Localization.Message("event.teamfight.winner", "&aTeam %team% &awins, and gets &6&l2x coins &afor the next &630 minutes&a. &lCongratulations!");
    public static final Localization.Message TEAMFIGHT_PARTICIPATE = new Localization.Message("event.teamfight-participate", "You have received %amount% Event Tokens for participating!");
    public static final Localization.Message TEAMFIGHT_WON = new Localization.Message("event.teamfight-won", " You have received %amount% Event Tokens for winning!");

    //KitPvP Kill Messages
    public static final Localization.Message NO_REWARDS = new Localization.Message("kill.no-rewards", "&7You killed the same player more than 3 times, &cno rewards &7rewarded.");
    public static final Localization.Message ASSIST_REWARD = new Localization.Message("kill.assist-rewards", "&7You got &6%amount% &7coins for assisting to kill %player%&7!");
    public static final Localization.Message KILLED_BY = new Localization.Message("kill.killed-by", "&7You were killed by &a%player%&7.");
    public static final Localization.Message YOU_KILLED = new Localization.Message("kill.you-killed", "&7You killed &a%player%&7.");
    public static final Localization.Message COLLECTED_BOUNTY = new Localization.Message("kill.collected-bounty", "&aYou got &a%amount% &7extra coins for collecting %player%'s bounty!");
    public static final Localization.Message BROKE_KILLSTREAK = new Localization.Message("kill.broke-killstreak", "&aYou got &a%amount% &7extra coins for breaking %player%'s killstreak!");

    //Kit Messages
    public static final Localization.Message CANT_USE_HERE = new Localization.Message("kit.cant-use-here", "&cYou can't use that ability here.");
    public static final Localization.Message YOURE_UNFROZEN = new Localization.Message("kit.unfrozen", "&aThawed out!");
    public static final Localization.Message LOVE_U = new Localization.Message("kit.lover.love-u", "&cI LOVE YOU <3!");
    public static final Localization.Message HEALTH_BOOST = new Localization.Message("kit.archer.health-boost", "&cHealth boost!");
    public static final Localization.Message DOUBLE_DAMAGE = new Localization.Message("kit.archer.double-damage", "&eDouble damage!");
    public static final Localization.Message DOUBLE_DAMAGE_YOU = new Localization.Message("kit.archer.double-damage-you", "&e%player% got double damage on you!");
    public static final Localization.Message TARGET_SLOWED = new Localization.Message("kit.archer.target-slowed", "&0Target is slowed.");
    public static final Localization.Message YOURE_SLOWED = new Localization.Message("kit.archer.youre-slowed", "&0%player% slowed you!");
    public static final Localization.Message TARGET_BLINDED = new Localization.Message("kit.archer.target-blinded", "&5Target blinded.");
    public static final Localization.Message YOURE_BLINDED = new Localization.Message("kit.lover.youre-blinded", "&5You got blinded by %player%!");
    public static final Localization.Message CURRENT_COMBO = new Localization.Message("kit.strafe.current-combo", "&7Current combo is &a&combo&&7.");
    public static final Localization.Message GET_SPOOKED = new Localization.Message("kit.ghost.get-spooked", "&fGet spooked!");
    public static final Localization.Message COPIED_KIT = new Localization.Message("kit.kitmaster.copied-kit", "&bYou copied the%kit% kit! You will return to the KitMaster kit in 20 seconds.");
    public static final Localization.Message COPIED_YOUR_KIT = new Localization.Message("kit.kitmaster.copied-your-kit", "&b%player% &fcopied your kit!");
    public static final Localization.Message SOUP_REFILL = new Localization.Message("kit.chance.soup-refill", "&bSoup refill!");
    public static final Localization.Message SWING_SPEED_UP = new Localization.Message("kit.chance.swing-speed-up", "&fSwing speed up!");
    public static final Localization.Message SWING_SPEED_DOWN = new Localization.Message("kit.chance.swing-speed-down", "&fSwing speed down!");
    public static final Localization.Message BACKFIRE = new Localization.Message("kit.chance.backfire", "&4Backfire!");
    public static final Localization.Message DEFENCE_UP = new Localization.Message("kit.unicorn.defence-up", "&fDefence up!");
    public static final Localization.Message CAT = new Localization.Message("kit.mystic.cat", "&[Cat]: %effect%!");
    public static final Localization.Message WOOSH = new Localization.Message("kit.enderman.woosh", "&5Woosh!");
    public static final Localization.Message BLEED_ACTIVATED = new Localization.Message("kit.huntsman.bleed-activated", "&cBleed activated!");
    public static final Localization.Message BLEED_DEACTIVATED = new Localization.Message("kit.huntsman.bleed-deactivated", "&7Bleed deactivated!");
    public static final Localization.Message BACKSTABBED = new Localization.Message("kit.shaco.backstabbed", "&cYou got backstabbed!");
    public static final Localization.Message POOF = new Localization.Message("kit.shaco.poof", "&7Poof!");
    public static final Localization.Message POSITIONS_SWITCHED = new Localization.Message("kit.shaco.positions-switched", "&9Positions switched!");
    public static final Localization.Message HEALED = new Localization.Message("kit.vampire.healed", "&cHealed!");
    public static final Localization.Message BIT_BY = new Localization.Message("kit.vampire.bit-by", "&cYou are bit by &f%player%&c.");
    public static final Localization.Message YOURE_FROZEN = new Localization.Message("kit.dualblader.frozen", "&aYou were frozen in place!");
    public static final Localization.Message PLAYER_HEALED = new Localization.Message("kit.medic.player-healed", "&c%player% was healed!");
    public static final Localization.Message FIRE_REMOVED = new Localization.Message("kit.firearcher.fire-removed", "&cFire removed.!");




    static void init() {
        Localization.getInstance().registerMessages("skycade.kitpvp",
                COULDNT_FIND,
                ON_COOLDOWN,
                USING_KIT,
                CANNOT_USE,
                NOT_ENOUGH,
                ALL_KITS_UNLOCKED,
                NO_SUCH_EVENT,
                TEAMFIGHT_START,
                TEAMFIGHT_TEAM1,
                TEAMFIGHT_TEAM2,
                TEAMFIGHT_FINAL_STATS,
                TEAMFIGHT_DRAW,
                TEAMFIGHT_WINNER,
                TEAMFIGHT_STARTING,
                TEAMFIGHT_PARTICIPATE,
                TEAMFIGHT_WON,
                TAG_START,
                KILLTHEKING_START,
                KILLTHEKING_TOO_LONG,
                KILLTHEKING_PARTICIPATE,
                KILLTHEKING_WON,
                KILLTHEKING_KILLED_BY,
                KILLTHEKING_KILLED,
                KILLTHEKING_LOGGED_OUT,
                KILLTHEKING_TO_SPAWN,
                KILLTHEKING_ENDED,
                DOUBLECREDITS_START,
                DOUBLECREDITS_ENDED,
                NO_LAST_KILLSTREAK,
                CURRENT_KILlSTREAK_HIGHER,
                KILLSTREAK_REFUNDED,
                KIT_UNLOCKED,
                YOUR_KIT_UNLOCKED,
                KIT_LOCKED,
                YOUR_KIT_LOCKED,
                CURRENCY_RESET,
                YOUR_CURRENCY_RESET,
                CURRENCY_ADDED,
                YOUR_CURRENCY_ADDED,
                CURRENCY_REMOVED,
                YOUR_CURRENCY_REMOVED,
                NO_REWARDS,
                ASSIST_REWARD,
                KILLED_BY,
                YOU_KILLED,
                COLLECTED_BOUNTY,
                BROKE_KILLSTREAK,
                STATS_RESET,
                STAT_SET,
                YOUR_STAT_SET,
                STATS,
                LOVE_U,
                HEALTH_BOOST,
                DOUBLE_DAMAGE,
                DOUBLE_DAMAGE_YOU,
                TARGET_SLOWED,
                YOURE_SLOWED,
                TARGET_BLINDED,
                YOURE_BLINDED,
                CANT_USE_HERE,
                CURRENT_COMBO,
                GET_SPOOKED,
                COPIED_KIT,
                COPIED_YOUR_KIT,
                SOUP_REFILL,
                SWING_SPEED_UP,
                SWING_SPEED_DOWN,
                BACKFIRE,
                CAT,
                WOOSH,
                DEFENCE_UP,
                BLEED_ACTIVATED,
                BLEED_DEACTIVATED,
                BACKSTABBED,
                POOF,
                POSITIONS_SWITCHED,
                HEALED,
                BIT_BY,
                YOURE_FROZEN,
                PLAYER_HEALED,
                FIRE_REMOVED,
                ON_COOLDOWN_NO_TIME,
                OFF_COOLDOWN,
                NOT_LOADED,
                ALREADY_USING,
                KIT_EQUIPPED,
                KIT_EQUIPPED_RESPAWN,
                YOU_PURCHASED,
                DIDNT_CHOOSE,
                NOT_ENOUGH_CURRENCY,
                KIT_DISABLED,
                ALREADY_UNLOCKED
        );
    }
}
