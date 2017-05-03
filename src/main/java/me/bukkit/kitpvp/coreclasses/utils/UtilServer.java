package me.bukkit.kitpvp.coreclasses.utils;

import org.bukkit.Bukkit;

public class UtilServer {

    public final static String CB_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    public final static String VERSION = CB_PACKAGE.substring(CB_PACKAGE.lastIndexOf('.') + 1);
    public final static String NMS_PACKAGE = "net.minecraft.server." + VERSION + ".";

}