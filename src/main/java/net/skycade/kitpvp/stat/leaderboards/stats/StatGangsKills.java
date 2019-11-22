package net.skycade.kitpvp.stat.leaderboards.stats;

import net.brcdev.gangs.GangsPlugin;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.ArrayList;
import java.util.List;

public class StatGangsKills extends StatisticType<String, Integer> {

    public StatGangsKills() {
        super("gangs-kills");
        List<String> gangNames = new ArrayList<>();
        for(Gang g : GangsPlusApi.getAllGangs()){
            gangNames.add(g.getName());
        }
        update(gangNames, true);
    }

    @Override
    public Integer get(String s) {
        int kills = GangsPlugin.getInstance().gangManager.getGang(s).getKills();
        return kills;
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
