package net.skycade.kitpvp.playerevents.commands;

import net.skycade.SkycadeCore.utility.command.SkycadeCommand;
import net.skycade.SkycadeCore.utility.command.addons.Permissible;
import net.skycade.kitpvp.KitPvP;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Permissible("kitpvp.admin")
public class CommandEventEdit extends SkycadeCommand {
    public CommandEventEdit() {
        super("eventedit");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
            player.sendMessage("/eventedit (event) (...)");
            player.sendMessage(" ");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "sumo": {
                if (args.length == 1) {
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                    player.sendMessage("/eventedit Sumo lobby - Sets the lobby position to your location");
                    player.sendMessage("/eventedit Sumo player1 - Sets the player-1 (fighter) position to your location");
                    player.sendMessage("/eventedit Sumo player2 - Sets the player-2 (fighter) position to your location");
                    player.sendMessage(" ");
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "lobby": {
                        KitPvP.getInstance().getEventManager().getSumoEvent().setLobbyLocation(player.getLocation());
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Lobby location of Sumo");
                        player.sendMessage(" ");
                        return;
                    }
                    case "player1": {
                        KitPvP.getInstance().getEventManager().getSumoEvent().setFighter1Location(player.getLocation());

                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Player (Fighter) 1 location of Sumo");
                        player.sendMessage(" ");
                        return;
                    }
                    case "player2": {
                        KitPvP.getInstance().getEventManager().getSumoEvent().setFighter2Location(player.getLocation());

                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Player (Fighter) 2 location of Sumo");
                        player.sendMessage(" ");
                        return;
                    }
                    default: {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("/eventedit Sumo lobby - Sets the lobby position to your location");
                        player.sendMessage("/eventedit Sumo player1 - Sets the player-1 (fighter) position to your location");
                        player.sendMessage("/eventedit Sumo player2 - Sets the player-2 (fighter) position to your location");
                        player.sendMessage(" ");
                        return;
                    }
                }
            }
            case "lms": {
                if (args.length == 1) {
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                    player.sendMessage("/eventedit LMS lobby - Sets the lobby position to your location");
                    player.sendMessage("/eventedit Sumo arena - Sets the areba position to your location");
                    player.sendMessage(" ");
                    return;
                }

                switch (args[1].toLowerCase()) {
                    case "lobby": {
                        KitPvP.getInstance().getEventManager().getLMS().setLobbyLocation(player.getLocation());

                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Lobby location of LMS");
                        player.sendMessage(" ");
                        return;
                    }
                    case "arena": {
                        KitPvP.getInstance().getEventManager().getLMS().setArenaLocation(player.getLocation());

                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Arena location of LMS");
                        player.sendMessage(" ");
                        return;
                    }
                    default: {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("/eventedit LMS lobby - Sets the lobby position to your location");
                        player.sendMessage("/eventedit Sumo arena - Sets the areba position to your location");
                        player.sendMessage(" ");
                        return;
                    }
                }
            }
            case "brackets": {
                if (args.length == 1) {
                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                    player.sendMessage("/eventedit Brackets lobby - Sets the lobby position to your location");
                    player.sendMessage("/eventedit Brackets player1 - Sets the player-1 (fighter) position to your location");
                    player.sendMessage("/eventedit Brackets player2 - Sets the player-2 (fighter) position to your location");
                    player.sendMessage(" ");
                    return;
                }
                switch (args[1].toLowerCase()) {
                    case "lobby": {
                        KitPvP.getInstance().getEventManager().getBrackets().setLobbyLocation(player.getLocation());

                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Lobby location of Brackets");
                        player.sendMessage(" ");
                        return;
                    }
                    case "player1": {
                        KitPvP.getInstance().getEventManager().getBrackets().setFighter1Location(player.getLocation());

                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Player (Fighter) 1 location of Brackets");
                        player.sendMessage(" ");
                        return;
                    }
                    case "player2": {
                        KitPvP.getInstance().getEventManager().getBrackets().setFighter2Location(player.getLocation());

                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("Successfully set Player (Fighter) 2 location of Brackets");
                        player.sendMessage(" ");
                        return;
                    }
                    default: {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.AQUA + "Skycade Events Editor");
                        player.sendMessage("/eventedit Brackets lobby - Sets the lobby position to your location");
                        player.sendMessage("/eventedit Brackets player1 - Sets the player-1 (fighter) position to your location");
                        player.sendMessage("/eventedit Brackets player2 - Sets the player-2 (fighter) position to your location");
                        player.sendMessage(" ");
                        return;
                    }
                }
            }

        }
    }


}