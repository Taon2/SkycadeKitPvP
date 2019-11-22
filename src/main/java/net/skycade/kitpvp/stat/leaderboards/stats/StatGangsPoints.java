package net.skycade.kitpvp.stat.leaderboards.stats;

import net.brcdev.gangs.GangsPlugin;
import net.brcdev.gangs.GangsPlusApi;
import net.brcdev.gangs.gang.Gang;
import net.skycade.kitpvp.KitPvP;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.ArrayList;
import java.util.List;

public class StatGangsPoints extends StatisticType<String, Integer> {

    public StatGangsPoints() {
        super("gangs-poins");
        List<String> gangNames = new ArrayList<>();
        for(Gang g : GangsPlusApi.getAllGangs()){
            gangNames.add(g.getName());
        }
        update(gangNames, true);
    }


    @Override
    public Integer get(String s) {
        return KitPvP.getInstance().getGangPointsManager().getPoints(s);
    }

    @Override
    public String keyToString(String s) {
        return s;
    }

    @Override
    public String valueToString(Integer aDouble) {
        return Double.toString(aDouble);
    }
}
