package net.skycade.kitpvp.stat.leaderboards.caching.automatic;

import net.skycade.kitpvp.KitPvP;
import net.skycade.skycadeleaderboards.SkycadeLeaderboards;
import org.bukkit.Bukkit;

public class Update {

    private int taskID = -1;

    public int getTaskID() {
        return taskID;
    }

    @SuppressWarnings("all")
    public boolean startTask() {
        if (!Bukkit.getScheduler().isCurrentlyRunning(taskID) && taskID == -1) {
            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SkycadeLeaderboards.getInstance(), new UpdateRunnable(), 0L, KitPvP.getInstance().getConfig().getInt("update-delay"));
            return true;
        }
        return false;
    }

    public boolean stopTask(){
        if(taskID != -1){
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
            return true;
        }
        return false;
    }
}
