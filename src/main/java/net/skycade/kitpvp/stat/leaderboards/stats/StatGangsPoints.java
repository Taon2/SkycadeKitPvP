package net.skycade.kitpvp.stat.leaderboards.stats;

import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.kitpvp.KitPvP;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.ArrayList;
import java.util.List;

public class StatGangsPoints extends StatisticType<String, Integer> {

    private static StatGangsPoints instance;

    private StatGangsPoints() {
        super("gangs-points");
        List<String> gangNames = new ArrayList<>();
        for (Gang g : GangsPlusApi.getAllGangs()) {
            gangNames.add(g.getName());
        }
        update(gangNames, true);
    }

    public static StatGangsPoints getInstance() {
        if (instance == null)
            instance = new StatGangsPoints();
        return instance;
    }

    @Override
    public Integer get(String s) {
        Integer points = KitPvP.getInstance().getGangPointsManager().getPoints(s);
        if (points == null) {
            return 0;
        } else {
            return points;
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
