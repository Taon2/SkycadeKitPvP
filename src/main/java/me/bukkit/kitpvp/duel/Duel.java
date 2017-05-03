package me.bukkit.kitpvp.duel;

import me.bukkit.kitpvp.coreclasses.region.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Duel {

	private Region region;
	private Location[] locations = new Location[2];
	private boolean isRunning = false;
	private List<UUID> players;
	
	private int hitCounter1 = 0;
	private int hitCounter2 = 0;
	
	public Duel(Region region, Location loc1, Location loc2) {
		this.region = region;
		this.locations[0] = loc1;
		this.locations[1] = loc2;
		players = new ArrayList<>();
	}
	
	public Region getRegion() {
		return region;
	}
	
	public Location[] getLocations() {
		return locations; 
	}
	
	public void setRunning(boolean value) {
		this.isRunning = value;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public List<UUID> getPlayers() {
		return players;
	}
	
	public void addPlayers(Player p1, Player p2) {
		players = new ArrayList<>();
		players.add(p1.getUniqueId());
		players.add(p2.getUniqueId());
	}
	
	public void incCounter1() {
	    hitCounter1++;
	}
	
	public void incCounter2() {
        hitCounter2++;
    }
	
	public void resetCounters() {
	    hitCounter1 = 0;
	    hitCounter2 = 0;
	}
	
	public int getHitCounter1() {
	    return hitCounter1;
	}
	
	public int getHitCounter2() {
	    return hitCounter2;
	}
	
}
