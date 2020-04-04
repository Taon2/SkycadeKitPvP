package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.kitpvp.KitPvP;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.ArrayList;
import java.util.List;

public class StatGangsPoints extends StatisticType<String, Long> {

    private static StatGangsPoints instance;

    private StatGangsPoints() {
        super("gangs-points");
        List<String> gangNames = new ArrayList<>(KitPvP.getInstance().getGangPointsManager().getAllGangNames());
        update(gangNames, true);
    }

    public static StatGangsPoints getInstance() {
        if (instance == null)
            instance = new StatGangsPoints();
        return instance;
    }

    @Override
    public Long get(String s) {
        Long points = KitPvP.getInstance().getGangPointsManager().getPoints(s);
        if (points == null) {
            return 0L;
        } else {
            return points;
        }
    }

    @Override
    public String keyToString(String s) {
        return s;
    }

    @Override
    public String valueToString(Long lng) {
        return Long.toString(lng);
    }
}
