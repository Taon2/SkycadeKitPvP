package net.skycade.kitpvp;

import net.skycade.SkycadeCore.Localization;

public class Messages {

    //KitPvP General Messages
    public static final Localization.Message ALL_KITS_UNLOCKED = new Localization.Message("all-kits-unlocked", "&7You unlocked &aall &7the kits! You unlocked the &aKitMaster &7kit.");
    public static final Localization.Message COULDNT_FIND = new Localization.Message("couldnt-find", "&cCould not find &b%type% &c'&b%thing%&c'.");
    public static final Localization.Message ON_COOLDOWN = new Localization.Message("on-cooldown",  "&cYou need to wait another &b%time% &cbefore using &b%thing% &cagain!");
    public static final Localization.Message ON_COOLDOWN_NO_TIME = new Localization.Message("on-cooldown-no-time", "&b%thing% &cis on cooldown!");
    public static final Localization.Message OFF_COOLDOWN = new Localization.Message("off-cooldown", "&7You can now use &a%thing%&7.");
    public static final Localization.Message NOT_ENOUGH = new Localization.Message("not-enough", "&cYou don't have enough &b%thing%&7.");
    public static final Localization.Message CANNOT_USE = new Localization.Message("cannot-use", "&cYou cannot use &b%thing% &c%reason%!");
    public static final Localization.Message ALREADY_USING = new Localization.Message("already-using", "&cYou are already using kit &b%kit%&c.");
    public static final Localization.Message KIT_EQUIPPED = new Localization.Message("kit-equipped", "&7Equipped kit &a%kit%&7.");
    public static final Localization.Message KIT_EQUIPPED_RESPAWN = new Localization.Message("kit-equipped", "&7Equipped kit &a%kit%&7. It will be active after you respawn.");
    public static final Localization.Message YOU_PURCHASED = new Localization.Message("you-purchased", "&7You bought &a%thing% &7for &a%amount% &7%currency%.");
    public static final Localization.Message NOT_ENOUGH_CURRENCY = new Localization.Message("not-enough-currency", "&cYou don't have enough &b%currency% &cto purchase &b%thing%&c.");
    public static final Localization.Message KIT_DISABLED = new Localization.Message("kit-disabled", "&cThat kit is disabled.");
    public static final Localization.Message ALREADY_UNLOCKED = new Localization.Message("already-unlocked", "&cYou already have &b%thing% &cunlocked.");
    public static final Localization.Message KITS_ROTATED = new Localization.Message("kits-rotated", "&7Kits in the shop have &arotated&7!");
    public static final Localization.Message DONT_OWN = new Localization.Message("dont-own", "&cYou don't own &b%kit%&c, /shop to purchase!");

    //KitPvP Command Messages
    public static final Localization.Message USING_KIT = new Localization.Message("command.using-kit", "&a%player%&7 is using the &a%kitname%&7 kit.");
    public static final Localization.Message NO_LAST_KILLSTREAK = new Localization.Message("command.no-last-killstreak", "&cLast killstreak can't be found for %player%.");
    public static final Localization.Message CURRENT_KILlSTREAK_HIGHER = new Localization.Message("command.current-killstreak-higher", "&cCurrent killstreak is higher than last killstreak.");
    public static final Localization.Message KILLSTREAK_REFUNDED = new Localization.Message("command.killstreak-refunded", "&aYour killstreak was refunded.");
    public static final Localization.Message KIT_UNLOCKED = new Localization.Message("command.kit-unlocked", "&a%player% now has %kit% unlocked.");
    public static final Localization.Message YOUR_KIT_UNLOCKED = new Localization.Message("command.your-kit-unlocked", "&a%kit% &7was unlocked.");
    public static final Localization.Message KIT_LOCKED = new Localization.Message("command.kit-locked", "&a%player% now has %kit% locked.");
    public static final Localization.Message YOUR_KIT_LOCKED = new Localization.Message("command.your-kit-locked", "&a%kit% &7was locked.");
    public static final Localization.Message CURRENCY_RESET = new Localization.Message("command.currency-reset", "&7%player%'s %currency% were &creset&7.");
    public static final Localization.Message YOUR_CURRENCY_RESET = new Localization.Message("command.your-currency-reset", "&7Your %currency% were &creset&7.");
    public static final Localization.Message CURRENCY_ADDED = new Localization.Message("command.currency-added", "&a%amount% &7%currency% given to &a%player%&7.");
    public static final Localization.Message YOUR_CURRENCY_ADDED = new Localization.Message("command.your-currency-added", "&7You got &a%amount% &7%currency%, your total balance is now &a%total% &7%currency%.");
    public static final Localization.Message CURRENCY_REMOVED = new Localization.Message("command.currency-removed", "&a%player%'s &7balance was lowered by &a%amount% &7%currency% to &a%total%&7.");
    public static final Localization.Message YOUR_CURRENCY_REMOVED = new Localization.Message("command.your-currency-removed", "&7Your balance was lowered by &a%amount% &7%currency%.");
    public static final Localization.Message STATS_RESET = new Localization.Message("command.stats-reset", "&a%player%'s &7stats are reset.");
    public static final Localization.Message STAT_SET = new Localization.Message("command.stat-set", "&a%player%'s %stat% has been set to %amount%.");
    public static final Localization.Message YOUR_STAT_SET = new Localization.Message("command.your-stat-set", "&7Your %stat% has been set to %amount%.");
    public static final Localization.Message MAX_PRESTIGE = new Localization.Message("command.max-prestige", "&cYou are already the maximum prestige level!.");
    public static final Localization.Message NOT_THAT_PRESTIGE = new Localization.Message("command.not-that-prestige", "&cYou cannot rank up to that prestige yet.");
    public static final Localization.Message PLAYER_NOW_PRESTIGE = new Localization.Message("command.now-prestige", "&a&l%player% &7&lis now &a&lPrestige %num%!");
    public static final Localization.Message GANG_POINTS_RESET = new Localization.Message("command.gang-points-reset", "&a%gang%'s &7gang points are reset.");
    public static final Localization.Message SHIFT_ABILITIES = new Localization.Message("command.shift-abilities", "&aYour abilities now require Shift + Right Click to activate.");
    public static final Localization.Message NO_SHIFT_ABILITIES = new Localization.Message("command.no-shift-abilities", "&aYour abilities now require Right Click to activate.");


    //KitPvP Command Usages
    public static final Localization.Message SOUP_USAGE = new Localization.Message("usage.soup", "&7/soup");
    public static final Localization.Message REFRESHKIT_USAGE = new Localization.Message("usage.refreshkit", "&7/refreshkit");
    public static final Localization.Message KIT_USAGE = new Localization.Message("usage.kit", "&7/kit");
    public static final Localization.Message SHOP_USAGE = new Localization.Message("usage.shop", "&7/shop");
    public static final Localization.Message EVENTSHOP_USAGE = new Localization.Message("usage.eventshop", "&7/eventshop");
    public static final Localization.Message PRESTIGE_USAGE = new Localization.Message("usage.prestige", "&7/prestige");
    public static final Localization.Message KITNAME_USAGE = new Localization.Message("usage.kitname", "&7/kitname &a<player>");
    public static final Localization.Message VIEWKIT_USAGE = new Localization.Message("usage.viewkit", "&7/viewkit &a<kitname>");
    public static final Localization.Message VIEWSTATS_USAGE = new Localization.Message("usage.viewstats", "&7/viewstats &a<player>");
    public static final Localization.Message TRIGGEREVENT_USAGE = new Localization.Message("usage.triggerevent", "&7/triggerevent &a<event>");
    public static final Localization.Message ECO_USAGE = new Localization.Message("usage.eco", "&7/eco &a<give/take/reset> <player/all> <amount>");
    public static final Localization.Message EVENTECO_USAGE = new Localization.Message("usage.eventeco", "&7/eventeco &a<give/take/reset> <player/all> <amount>");
    public static final Localization.Message REFUNDKS_USAGE = new Localization.Message("usage.refundks", "&7/refundks &a<player>");
    public static final Localization.Message SETSTATS_USAGE = new Localization.Message("usage.setstats", "&7/setstat &a<player> <stat> <amount>");
    public static final Localization.Message RESETSTATS_USAGE = new Localization.Message("usage.resetstats", "&7/resetstats &a<player>");
    public static final Localization.Message LOCK_UNLOCK_USAGE = new Localization.Message("usage.lock-unlock", "&7/kit &a<lock/unlock> <player/all> <kitname>");
    public static final Localization.Message KITSUNLOCKED_USAGE = new Localization.Message("usage.kitsunlocked", "&7/kitsunlocked &a<player>");
    public static final Localization.Message RESETGANGPOINTS_USAGE = new Localization.Message("usage.resetgangpoints", "&7/resetgangpoints &a<gangname/all>");
    public static final Localization.Message ABILITYTOGGLE_USAGE = new Localization.Message("usage.abilitytoggle", "&7/abilitytoggle");

    public static final Localization.Message KITPVPHELP_TITLE = new Localization.Message("title.kitpvphelp", "&7------------------&2KitPvP Help&7------------------");
    public static final Localization.Message SOUP_DESCRIPTION = new Localization.Message("description.soup", "&7Gives the player soup.");
    public static final Localization.Message REFRESHKIT_DESCRIPTION = new Localization.Message("description.refreshkit", "&7Refreshes a players current kit.");
    public static final Localization.Message KIT_DESCRIPTION = new Localization.Message("description.kit", "&7Opens the kit menu.");
    public static final Localization.Message SHOP_DESCRIPTION = new Localization.Message("description.shop", "&7Opens the shop menu.");
    public static final Localization.Message EVENTSHOP_DESCRIPTION = new Localization.Message("description.eventshop", "&7Opens the eventshop menu.");
    public static final Localization.Message PRESTIGE_DESCRIPTION = new Localization.Message("description.prestige", "&7Opens the prestige menu.");
    public static final Localization.Message KITNAME_DESCRIPTION = new Localization.Message("description.kitname", "&7Displays the kit a player is currently using.");
    public static final Localization.Message VIEWKIT_DESCRIPTION = new Localization.Message("description.viewkit", "&7Displays the contents of a kit.");
    public static final Localization.Message VIEWSTATS_DESCRIPTION = new Localization.Message("description.viewstats", "&7Displays the stats of a player.");
    public static final Localization.Message TRIGGEREVENT_DESCRIPTION = new Localization.Message("description.triggerevent", "&7Starts an event.");
    public static final Localization.Message ECO_DESCRIPTION = new Localization.Message("description.eco", "&7Changes a player's coin balance.");
    public static final Localization.Message EVENTECO_DESCRIPTION = new Localization.Message("description.eventeco", "&7Changes a player's event token balance.");
    public static final Localization.Message REFUNDKS_DESCRIPTION = new Localization.Message("description.refundks", "&7Gives that player their previous killstreak.");
    public static final Localization.Message SETSTATS_DESCRIPTION = new Localization.Message("description.setstats", "&7Sets a player's kills, deaths, killstreak, highestkillstreak, or assists.");
    public static final Localization.Message RESETSTATS_DESCRIPTION = new Localization.Message("description.resetstats", "&7Resets all stats for a player.");
    public static final Localization.Message LOCK_UNLOCK_DESCRIPTION = new Localization.Message("description.lock-unlock", "&7Locks or unlocks a kit for a player.");
    public static final Localization.Message KITSUNLOCKED_DESCRIPTION = new Localization.Message("description.kitsunlocked", "&7Displays all kits unlocked by a player.");
    public static final Localization.Message RESETGANGPOINTS_DESCRIPTION = new Localization.Message("description.resetgangpoints", "&7Resets the points of a gang.");
    public static final Localization.Message ABILITYTOGGLE_DESCRIPTION = new Localization.Message("description.abilitytoggle", "&7Toggles abilities between requiring the use of Shift to activate.");

    //KitPvP Stats
    public static final Localization.Message STATS = new Localization.Message("command.your-stat-set",
            "&7---------- &f&l%player%'s KitPvP Stats &7----------\n" +
                    "&fGang Affiliation - &a%gang%\n" +
                    "&fDeaths - &a%deaths%\n" +
                    "&fKills - &a%kills%\n" +
                    "&fK/D - &a%kdr%\n" +
                    "&fAssists - &a%assists%\n" +
                    "&fCurrent Killstreak - &a%currentkillstreak%\n" +
                    "&fHighest Killstreak - &a%highestkillstreak%\n" +
                    "&fKits - &a%kits%\n" +
                    "&fCurrent Kit - &a%currentkit%\n" +
                    "&fCoins - &a%coins%\n" +
                    "&fEvent Tokens - &a%eventtokens%\n"
            );

    //KitPvP Event Messages
    public static final Localization.Message NO_SUCH_EVENT = new Localization.Message("event.no-such-event", "&cNo such event.");
    public static final Localization.Message EVENT_ALREADY_RUNNING = new Localization.Message("event.already-running", "&cAn event is already running!");

    //Double Credits
    public static final Localization.Message DOUBLECREDITS_START = new Localization.Message("event.doublecredits-start", "&2&lDOUBLE CREDITS! &r&aFor the next 30 minutes, everybody earns double the amount of coins!");
    public static final Localization.Message DOUBLECREDITS_ENDED = new Localization.Message("event.doublecredits-ended", "&2&lDOUBLE CREDITS ENDED!");

    //Kill The King
    public static final Localization.Message KILLTHEKING_START = new Localization.Message("event.killtheking-start", "&6&lKILL THE KING! &r&a%player% is the King. Everyone attack them!");
    public static final Localization.Message KILLTHEKING_TOO_LONG = new Localization.Message("event.killtheking-too-long", "&6&lKILL THE KING! &aThe &6King &atook too long to kill.");
    public static final Localization.Message KILLTHEKING_PARTICIPATE = new Localization.Message("event.killtheking-participate", "&7You have received &a%amount% &7Event Tokens for participating!");
    public static final Localization.Message KILLTHEKING_SURVIVED = new Localization.Message("event.killtheking-survived", "&7You have received &a%amount% &7Event Tokens for surviving!");
    public static final Localization.Message KILLTHEKING_WON = new Localization.Message("event.killtheking-won", "&7You have killed the King! You have received &a%amount% &7Event Tokens for winning!");
    public static final Localization.Message KILLTHEKING_KILLED_BY = new Localization.Message("event.killtheking-killed-by", "&6&lKILL THE KING! &6%player% &7has killed the King!");
    public static final Localization.Message KILLTHEKING_KILLED = new Localization.Message("event.killtheking-killed", "&aThe King has been killed!");
    public static final Localization.Message KILLTHEKING_LOGGED_OUT = new Localization.Message("event.killtheking-logged-out", "&aThe King has been killed!");
    public static final Localization.Message KILLTHEKING_TO_SPAWN = new Localization.Message("event.killtheking-to-spawn", "&aThe King has been killed!");
    public static final Localization.Message KILLTHEKING_ENDED = new Localization.Message("event.killtheking-ended", "&6&lKILL THE KING! &7The event has &cended&7.");

    //Tag
    public static final Localization.Message TAG_START = new Localization.Message("event.tag-start", "&a&lINFECTION! &r&a%player% is infected. Stay away from them for 5 minutes to get a coin bonus!");
    public static final Localization.Message TAG_ENDED = new Localization.Message("event.tag-ended", "&a&lINFECTION ENDED!");

    //Capture The Flag
    public static final Localization.Message CAPTURETHEFLAG_START = new Localization.Message("event.capturetheflag-start", "&a&lCAPTURE THE FLAG! &r&aTake the flag for your own!");
    public static final Localization.Message CAPTURETHEFLAG_STARTING = new Localization.Message("event.capturetheflag.starting", "&a&lCAPTURE THE FLAG &astarting in &6%time%&a!");
    public static final Localization.Message CAPTURETHEFLAG_TEAM = new Localization.Message("event.capturetheflag.team", "&a&lYou have been assigned to team %team%&a!\n" +
            "&aSteal the flag in the center and bring it to your capture point. The team with the most captures at the end gets &6&l2x coins &aat the end for 30 minutes and &6&l5 event tokens&a!");
    public static final Localization.Message CAPTURETHEFLAG_POINT = new Localization.Message("event.capturetheflag.point", "%player% &ahas gained a point for the %team% &ateam!");
    public static final Localization.Message CAPTURETHEFLAG_DROPPED_FLAG = new Localization.Message("event.capturetheflag.dropped-flag", "&a&l%player% &ahas dropped the flag!");
    public static final Localization.Message CAPTURETHEFLAG_CANT_CARRY = new Localization.Message("event.capturetheflag.cant-carry", "&cYou cannot carry the flag now! %reason%.");
    public static final Localization.Message CAPTURETHEFLAG_PICKED_UP_FLAG = new Localization.Message("event.capturetheflag.picked-up-flag", "%player% &ahas picked up the flag!");
    public static final Localization.Message CAPTURETHEFLAG_FINAL_STATS = new Localization.Message("event.capturetheflag.final-stats", "&a&lThe Capture The Flag event is over! &aHere are the results:\n" +
            "&bTeam &c&lRED&b: &6%points1% Points\n" +
            "&bTeam &9&lBLUE&b: &6%points2% Points");
    public static final Localization.Message CAPTURETHEFLAG_DRAW = new Localization.Message("event.capturetheflag.draw", "&aThe event ended with a &e&lDRAW&a! Thanks to everyone for participating!");
    public static final Localization.Message CAPTURETHEFLAG_WINNER = new Localization.Message("event.capturetheflag.winner", "&aTeam %team% &awins, and gets &6&l2x coins &afor the next &630 minutes&a. &lCongratulations!");
    public static final Localization.Message CAPTURETHEFLAG_PARTICIPATE = new Localization.Message("event.capturetheflag-participate", "&7You have received &a%amount% &7Event Tokens for participating!");
    public static final Localization.Message CAPTURETHEFLAG_WON = new Localization.Message("event.capturetheflag-won", "&7You have received &a%amount% &7Event Tokens for winning!");
    public static final Localization.Message CAPTURETHEFLAG_OVERTIME = new Localization.Message("event.capturetheflag-overtime", "&a&lOVERTIME! &7The scores are tied! Next flag capture wins!");
    public static final Localization.Message CAPTURETHEFLAG_TOO_LONG = new Localization.Message("event.capturetheflag-too-long", "&a&lThe flag took too long to capture.");

    //KOTH
    public static final Localization.Message KNOCKBACK_REMOVED = new Localization.Message("knockback-removed", "&cThe Knockback on your weapon was removed due to KOTH being active.");

    //KitPvP Kill Messages
    public static final Localization.Message NO_REWARDS = new Localization.Message("kill.no-rewards", "&7You have killed &a%player% &7more than 3 times in a row.\n" +
            "&cNo rewards &7rewarded.");

    public static final Localization.Message ASSIST_REWARD = new Localization.Message("kill.assist-rewards", "&7You got &6%amount% &7coins for assisting to kill %player%&7!");
    public static final Localization.Message KILLED_BY = new Localization.Message("kill.killed-by", "&7You have been killed by &c%player%&7.");
    public static final Localization.Message YOU_KILLED = new Localization.Message("kill.you-killed", "&7You have killed &a%player%&7 and earned &6%coins% Coins&7.");
    public static final Localization.Message KILLED_GANG_MEMBER = new Localization.Message("kill.killed-gang-member", "&7You have killed a fellow gang member, so you will not be rewarded.");
    public static final Localization.Message YOU_KILLED_LOGGED_OUT = new Localization.Message("kill.you-killed-logged-out", "&7You killed &a%player%&7 because they logged out while in combat with you.");
    public static final Localization.Message COLLECTED_BOUNTY = new Localization.Message("kill.collected-bounty", "&aYou got &a%amount% &7extra coins for collecting %player%'s bounty!");
    public static final Localization.Message YOU_BROKE_KILLSTREAK = new Localization.Message("kill.you-broke-killstreak", "&7You got &a%amount% &7extra coins for breaking &a%player%'s &7killstreak!");
    public static final Localization.Message BROKE_KILLSTREAK = new Localization.Message("kill.broke-killstreak", "&c&l%killer% &6has broken &c&l%dead%'s &6killstreak of &c&l%ks%&6!");
    public static final Localization.Message HAS_KILLSTREAK = new Localization.Message("kill.has-killstreak", "&c&l%killer% &6has reached a killstreak of &c&l%ks%&6!");

    // Anti-Phase
    public static final Localization.Message ANTI_PHASE_SENT_TO_SPAWN = new Localization.Message("anti-phase.sent-to-spawn", "&7You have been detected inside of a block and have been sent to Spawn.");
    public static final Localization.Message ANTI_PHASE_COMBAT_EVASION = new Localization.Message("anti-phase.combat-evasion", "&7You have been detected inside of a block.\n" +
            "&7You were in combat at the time of glitching inside of this block, therefore you have &cdied&7!");


    //Kit Messages
    public static final Localization.Message CANT_USE_HERE = new Localization.Message("kit.cant-use-here", "&cYou can't use that ability here.");
    public static final Localization.Message YOURE_UNFROZEN = new Localization.Message("kit.unfrozen", "&bThawed out!");
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
    public static final Localization.Message COPIED_KIT = new Localization.Message("kit.kitmaster.copied-kit", "&bYou copied the %kit% kit! You will return to the KitMaster kit in 20 seconds.");
    public static final Localization.Message COPIED_YOUR_KIT = new Localization.Message("kit.kitmaster.copied-your-kit", "&b%player% &7copied your kit!");
    public static final Localization.Message SOUP_REFILL = new Localization.Message("kit.chance.soup-refill", "&bSoup refill!");
    public static final Localization.Message SWING_SPEED_UP = new Localization.Message("kit.chance.swing-speed-up", "&fSwing speed up!");
    public static final Localization.Message SWING_SPEED_DOWN = new Localization.Message("kit.chance.swing-speed-down", "&fSwing speed down!");
    public static final Localization.Message BACKFIRE = new Localization.Message("kit.chance.backfire", "&4Backfire!");
    public static final Localization.Message DEFENCE_UP = new Localization.Message("kit.unicorn.defence-up", "&fDefence up!");
    public static final Localization.Message CAT = new Localization.Message("kit.mystic.cat", "&2[Cat]: %effect%!");
    public static final Localization.Message WOOSH = new Localization.Message("kit.enderman.woosh", "&5Woosh!");
    public static final Localization.Message BLEED_ACTIVATED = new Localization.Message("kit.huntsman.bleed-activated", "&cBleed activated!");
    public static final Localization.Message BLEED_DEACTIVATED = new Localization.Message("kit.huntsman.bleed-deactivated", "&7Bleed deactivated!");
    public static final Localization.Message BACKSTABBED = new Localization.Message("kit.shaco.backstabbed", "&cYou got backstabbed!");
    public static final Localization.Message POOF = new Localization.Message("kit.shaco.poof", "&7Poof!");
    public static final Localization.Message POSITIONS_SWITCHED = new Localization.Message("kit.shaco.positions-switched", "&9Positions switched!");
    public static final Localization.Message HEALED = new Localization.Message("kit.vampire.healed", "&cHealed!");
    public static final Localization.Message BIT_BY = new Localization.Message("kit.vampire.bit-by", "&cYou are bit by &f%player%&c.");
    public static final Localization.Message YOURE_FROZEN = new Localization.Message("kit.dualblader.frozen", "&bYou were frozen in place by &a%player% &busing &b%kit%!");
    public static final Localization.Message YOURE_POISONED = new Localization.Message("kit.shroom.poisoned", "&2You were poisoned by &b%player%&2!");
    public static final Localization.Message PLAYER_HEALED = new Localization.Message("kit.medic.player-healed", "&c%player% was healed!");
    public static final Localization.Message FIRE_REMOVED = new Localization.Message("kit.firearcher.fire-removed", "&cFire removed.!");
    public static final Localization.Message PLAYER_REPAIRED = new Localization.Message("kit.blacksmith.player-repaired", "&7You repaired &a%player%'s &7armor.");
    public static final Localization.Message REPAIRED = new Localization.Message("kit.blacksmith.repaired", "&7Armor repaired by &a%player%!");
    public static final Localization.Message REPAIR_DENY = new Localization.Message("kit.blacksmith.repaired", "&7You must be in the same gang as &a%player% &7to repair their armor.");
    public static final Localization.Message SUMMONED_GHOSTS = new Localization.Message("kit.necromancer.summoned-ghosts", "&7Summoned 2 ghosts to fight for you!");
    public static final Localization.Message YOURE_WITHERED = new Localization.Message("kit.necromancer.withered", "&8You were withered by &b%player%&8!");
    public static final Localization.Message TELEPORTED_GHOSTS = new Localization.Message("kit.necromancer.teleported-ghosts", "&7Teleported ghosts to &b%player%&7!");
    public static final Localization.Message DISGUISING = new Localization.Message("kit.blockhunt.transforming", "&7Stand still for &a3 &7seconds to disguise as this block!");
    public static final Localization.Message DISGUISED = new Localization.Message("kit.blockhunt.transformed", "&aDisguised!");
    public static final Localization.Message DISGUISE_REMOVED = new Localization.Message("kit.blockhunt.disguise-removed", "&7Disguise removed!");
    public static final Localization.Message PHYLACTERY_PLACED = new Localization.Message("kit.lich.phylactery-placed", "&7Phylactery placed! You will respawn at its location when you die, if it is still there.");
    public static final Localization.Message PHYLACTERY_EXPIRED = new Localization.Message("kit.lich.phylactery-expired", "&7Your phylactery has expired.");
    public static final Localization.Message PHYLACTERY_BROKEN = new Localization.Message("kit.lich.phylactery-broken", "&7Your phylactery was broken by &a%player%&7.");
    public static final Localization.Message YOU_BROKE_PHYLACTERY = new Localization.Message("kit.lich.you-broke-phylactery", "&7You broke &a%player%'s &7phylactery.");
    public static final Localization.Message PHYLACTERY_RESPAWNED = new Localization.Message("kit.lich.phylactery-respawned", "&7Respawned at your phylactery.");
    public static final Localization.Message FROZEN_ALREADY = new Localization.Message("kit.frosty.frozen-already", "&a%player% &chas already been frozen recently!");
    public static final Localization.Message PRICKED_RECENTLY = new Localization.Message("kit.prick.pricked-recently", "&a%player% &chas already been pricked recently!");
    public static final Localization.Message OUT_OF_AMMUNITION = new Localization.Message("kit.paintball.out-of-ammunition", "&cYou do not have any ammunition left! Please wait for some to regenerate.");
    public static final Localization.Message CANNOT_THROW_AMMUNITION = new Localization.Message("kit.paintball.cannot-throw-ammunition", "&cYou may not throw your ammunition. Wasteful!");

    static void init() {
        Localization.getInstance().registerMessages("skycade.kitpvp",
                COULDNT_FIND,
                ON_COOLDOWN,
                USING_KIT,
                CANNOT_USE,
                NOT_ENOUGH,
                ALL_KITS_UNLOCKED,
                NO_SUCH_EVENT,
                CAPTURETHEFLAG_START,
                CAPTURETHEFLAG_TEAM,
                CAPTURETHEFLAG_POINT,
                CAPTURETHEFLAG_FINAL_STATS,
                CAPTURETHEFLAG_DRAW,
                CAPTURETHEFLAG_WINNER,
                CAPTURETHEFLAG_STARTING,
                CAPTURETHEFLAG_PARTICIPATE,
                CAPTURETHEFLAG_WON,
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
                YOU_BROKE_KILLSTREAK,
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
                ALREADY_USING,
                KIT_EQUIPPED,
                KIT_EQUIPPED_RESPAWN,
                YOU_PURCHASED,
                NOT_ENOUGH_CURRENCY,
                KIT_DISABLED,
                ALREADY_UNLOCKED,
                MAX_PRESTIGE,
                NOT_THAT_PRESTIGE,
                DONT_OWN,
                EVENT_ALREADY_RUNNING,
                TRIGGEREVENT_USAGE,
                ECO_USAGE,
                EVENTECO_USAGE,
                KITSUNLOCKED_USAGE,
                REFUNDKS_USAGE,
                SETSTATS_USAGE,
                LOCK_UNLOCK_USAGE,
                TRIGGEREVENT_DESCRIPTION,
                ECO_DESCRIPTION,
                EVENTECO_DESCRIPTION,
                KITSUNLOCKED_DESCRIPTION,
                REFUNDKS_DESCRIPTION,
                SETSTATS_DESCRIPTION,
                LOCK_UNLOCK_DESCRIPTION,
                KITPVPHELP_TITLE,
                SOUP_USAGE,
                REFRESHKIT_USAGE,
                KIT_USAGE,
                SHOP_USAGE,
                EVENTSHOP_USAGE,
                PRESTIGE_USAGE,
                KITNAME_USAGE,
                VIEWKIT_USAGE,
                RESETSTATS_USAGE,
                SOUP_DESCRIPTION,
                REFRESHKIT_DESCRIPTION,
                KIT_DESCRIPTION,
                SHOP_DESCRIPTION,
                EVENTSHOP_DESCRIPTION,
                PRESTIGE_DESCRIPTION,
                KITNAME_DESCRIPTION,
                VIEWKIT_DESCRIPTION,
                RESETSTATS_DESCRIPTION,
                PLAYER_REPAIRED,
                REPAIRED,
                REPAIR_DENY,
                YOURE_POISONED,
                SUMMONED_GHOSTS,
                YOURE_WITHERED,
                TELEPORTED_GHOSTS,
                VIEWSTATS_USAGE,
                VIEWSTATS_DESCRIPTION,
                BROKE_KILLSTREAK,
                HAS_KILLSTREAK,
                DISGUISING,
                DISGUISED,
                DISGUISE_REMOVED,
                PHYLACTERY_PLACED,
                PHYLACTERY_EXPIRED,
                PHYLACTERY_BROKEN,
                YOU_BROKE_PHYLACTERY,
                PHYLACTERY_RESPAWNED,
                RESETGANGPOINTS_USAGE,
                RESETGANGPOINTS_DESCRIPTION,
                GANG_POINTS_RESET,
                YOU_KILLED_LOGGED_OUT,
                CAPTURETHEFLAG_OVERTIME,
                CAPTURETHEFLAG_TOO_LONG,
                KNOCKBACK_REMOVED,
                PLAYER_NOW_PRESTIGE,
                FROZEN_ALREADY,
                SHIFT_ABILITIES,
                NO_SHIFT_ABILITIES,
                PRICKED_RECENTLY,
                KILLED_GANG_MEMBER,
                ANTI_PHASE_COMBAT_EVASION,
                ANTI_PHASE_SENT_TO_SPAWN,
                OUT_OF_AMMUNITION,
                CANNOT_THROW_AMMUNITION
        );
    }
}
