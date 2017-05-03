package net.skycade.kitpvp.listeners.chat;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

public class ChatClick implements Listener {

	// random uuid
	private static final String PREFIX = "0e92d99e-f785-4ec9-be67-c5d343b27637";

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}

	private final Map<UUID, Map<UUID, Consumer<Player>>> chatClicks = Maps.newHashMap();

	public ChatClick() {
		((Logger) LogManager.getRootLogger()).addFilter(new Filter() {
			public Filter.Result filter(LogEvent event) {
				if (event.getMessage().toString().contains(PREFIX))
					return Filter.Result.DENY;
				return null;
			}

			public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String arg3, Object... arg4) {
				return null;
			}

			public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Object arg3, Throwable arg4) {
				return null;
			}

			public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Message arg3, Throwable arg4) {
				return null;
			}

			public Filter.Result getOnMatch() {
				return null;
			}

			public Filter.Result getOnMismatch() {
				return null;
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerCommandPreprocessEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();

		String message = e.getMessage().substring(1);
		if (!message.startsWith(PREFIX))
			return;
		e.setCancelled(true);

		message = message.substring(PREFIX.length()).trim();

		Map<UUID, Consumer<Player>> tasks = chatClicks.get(uuid);
		if (tasks == null)
			return;

		UUID taskId = UUID.fromString(message);

		Consumer<Player> consumer = tasks.get(taskId);

		if (consumer != null) {
			consumer.accept(Bukkit.getPlayer(uuid));
			tasks.remove(taskId);
		}
	}

	@EventHandler
	public void on(PlayerQuitEvent e) {
		chatClicks.remove(e.getPlayer().getUniqueId());
	}

	public String registerClick(Player p, Consumer<Player> onClick) {
		UUID chatClickId = UUID.randomUUID();
		chatClicks.computeIfAbsent(p.getUniqueId(), u -> Maps.newHashMap()).put(chatClickId, onClick);
		return "/" + PREFIX + " " + chatClickId.toString();
	}

}