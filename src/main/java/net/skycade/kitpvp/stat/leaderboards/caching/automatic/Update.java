package net.skycade.kitpvp.stat.leaderboards.caching.automatic;

import net.skycade.kitpvp.KitPvP;
import net.skycade.skycadeleaderboards.SkycadeLeaderboards;
import org.bukkit.Bukkit;

public class Update {

    private int taskID = -1;

    public int getTaskID() {
        return taskID;
    }

    public boolean startTask() {
        if (!Bukkit.getScheduler().isCurrentlyRunning(taskID) && taskID == -1) {
            taskID = Bukkit.getScheduler().runTaskLaterAsynchronously(SkycadeLeaderboards.getInstance(), new UpdateRunnable(),KitPvP.getInstance().getConfig().getInt("update-delay")).getTaskId();
            return true;
        }
        return false;
    }

    public boolean stopTask() {
        if(taskID != -1){
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
            return true;
        }
        return false;
    }
}
