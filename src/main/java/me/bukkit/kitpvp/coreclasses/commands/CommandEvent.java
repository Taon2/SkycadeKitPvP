package me.bukkit.kitpvp.coreclasses.commands;

import me.bukkit.kitpvp.coreclasses.events.CustomEvent;
import me.bukkit.kitpvp.coreclasses.member.Member;
import org.bukkit.entity.Player;

public class CommandEvent extends CustomEvent {

    private Player p;
    private Member member;
    private String command;
    private String[] args;

    public CommandEvent(Player p, Member member, String command, String... args) {
        this.p = p;
        this.member = member;
        this.command = command;
        this.args = args;
    }

    public Player getPlayer() {
        return p;
    }

    public Member getMember() {
        return member;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArguments() {
        return args;
    }

}
