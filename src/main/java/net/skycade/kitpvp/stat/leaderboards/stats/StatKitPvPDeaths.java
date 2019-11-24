package net.skycade.kitpvp.stat.leaderboards.stats;

import net.skycade.SkycadeCore.utility.MojangUtil;
import net.skycade.kitpvp.coreclasses.member.MemberManager;
import net.skycade.skycadeleaderboards.leaderboards.StatisticType;

import java.util.List;
import java.util.UUID;

public class StatKitPvPDeaths extends StatisticType<UUID, Integer> {

    private static StatKitPvPDeaths instance;

    private StatKitPvPDeaths() {
        super("kitpvp-deaths");
    }

    public static StatKitPvPDeaths getInstance() {
        if (instance == null)
            instance = new StatKitPvPDeaths();
        return instance;
    }

    public void init(List<UUID> uuids) {
        update(uuids, true);
    }

    @Override
    public Integer get(UUID s) {
        return MemberManager.getInstance().getMember(s, true).getDeaths();
    }

    @Override
    public String keyToString(UUID s) {
        MojangUtil.PlayerData data = MojangUtil.get(s);
        return data == null ? "unknown" : data.getName();
    }

    @Override
    public String valueToString(Integer integer) {
        return Integer.toString(integer);
    }
}
