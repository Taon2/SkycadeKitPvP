package net.skycade.kitpvp.events.teamfight;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import net.skycade.kitpvp.KitPvP;
import net.skycade.kitpvp.events.RandomEvent;
import net.skycade.kitpvp.events.TeamFightEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class TeamFightPacketListener implements PacketListener {
    @Override
    public void onPacketSending(PacketEvent event) {
        if (!(RandomEvent.getCurrent() instanceof TeamFightEvent)) return;

        Long begin = ((TeamFightEvent) RandomEvent.getCurrent()).getBegin();
        if (begin == null || begin > System.currentTimeMillis()) return;

        PacketContainer packet = event.getPacket();
        Integer eid = packet.getIntegers().read(0);
        Integer slot = packet.getIntegers().read(1);
        //ItemStack is = packet.getItemModifier().read(0);

        Player player = Bukkit.getOnlinePlayers().stream().filter(e -> e.getEntityId() == eid).findAny().orElse(null);

        if (player == null) return;

        if (slot == 4) {
            event.setCancelled(true);
            ItemStack banner = TeamFightEvent.getBannerFor(player.getUniqueId());
            if (banner == null) return;
            packet.getItemModifier().write(0, banner);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), packet, false);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {}

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return ListeningWhitelist.newBuilder()
                .types(PacketType.Play.Server.ENTITY_EQUIPMENT)
                .build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.EMPTY_WHITELIST;
    }

    @Override
    public Plugin getPlugin() {
        return KitPvP.getInstance();
    }
}
