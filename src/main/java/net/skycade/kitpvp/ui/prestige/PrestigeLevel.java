package net.skycade.kitpvp.ui.prestige;

import java.util.List;

public class PrestigeLevel {

    private int level;
    private int cost;
    private List<String> commands;
    private List<String> rewardDesc;

    PrestigeLevel(int level, int cost, List<String> commands, List<String> rewardDesc) {
        this.level = level;
        this.cost = cost;
        this.commands = commands;
        this.rewardDesc = rewardDesc;
    }

    public int getLevel() {
        return level;
    }

    public int getCost() {
        return cost;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getRewardDesc() {
        return rewardDesc;
    }
}
