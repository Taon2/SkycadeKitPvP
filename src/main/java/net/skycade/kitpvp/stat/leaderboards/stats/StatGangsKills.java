package net.skycade.kitpvp.stat.leaderboards.stats;

import net.brcdev.gangs.GangsPlugin;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.kitpvp.KitPvP;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.ArrayList;
import java.util.List;

public class StatGangsKills extends StatisticType<String, Integer> {

    private static StatGangsKills instance;

    private StatGangsKills() {
        super("gangs-kills");
        List<String> gangNames = new ArrayList<>();
        for (Gang g : GangsPlusApi.getAllGangs()) {
            gangNames.add(g.getName());
        }
        update(gangNames, true);
    }

    public static StatGangsKills getInstance() {
        if (instance == null)
            instance = new StatGangsKills();
        return instance;
    }

    @Override
    public Integer get(String s) {
        Integer kills = GangsPlugin.getInstance().gangManager.getGang(s).getKills();
        if (kills == null) {
            return 0;
        } else {
            return kills;
        }
    }

    @Override
    public String keyToString(String s) {
        return s;
    }

    @Override
    public String valueToString(Integer integer) {
        return Integer.toString(integer);
    }
}
