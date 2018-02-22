package net.skycade.kitpvp.coreclasses.utils;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.skycade.kitpvp.KitPvP;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class UtilPacket {

    public static TextComponent createTextComponent(String s, ChatColor colour) {
        return createTextComponent(s, colour, false);
    }

    public static TextComponent createTextComponent(String s, ChatColor colour, boolean bold) {
        TextComponent component = new TextComponent(s);
        if (colour != null)
            component.setColor(net.md_5.bungee.api.ChatColor.valueOf(colour.name()));
        component.setBold(bold);
        return component;
    }

    public static BaseComponent[] createChatComponent(Player p, String message, String hoverText, Consumer<Player> onClick) {
        BaseComponent[] components = TextComponent.fromLegacyText(message);

        HoverEvent hoverEvent = null;
        if (hoverText != null)
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText));

        ClickEvent clickEvent = null;
        if (onClick != null)
            clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, KitPvP.getInstance().getChatClick().registerClick(p, onClick));

        for (BaseComponent baseComponent : components) {
            if (hoverEvent != null)
                baseComponent.setHoverEvent(hoverEvent);
            if (clickEvent != null)
                baseComponent.setClickEvent(clickEvent);
        }

        return components;
    }

    public static BaseComponent[] concatinateChatComponents(BaseComponent[]... baseComponents) {
        List<BaseComponent> list = Lists.newLinkedList();

        for (BaseComponent[] array : baseComponents)
            Collections.addAll(list, array);

        return list.toArray(new BaseComponent[list.size()]);
    }

}