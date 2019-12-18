package net.skycade.kitpvp.coreclasses.utils;

import org.bukkit.block.Block;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class UtilMath {

    public static double trim(double d) {
        return trim(d, 1);
    }

    public static double trim(double d, int degree) {
        if (Double.isNaN(d) || Double.isInfinite(d))
            d = 0;
        String format = "#.#";
        for (int i = 1; i < degree; i++)
            format += "#";
        try {
            return Double.valueOf(new DecimalFormat(format).format(d));
        } catch (NumberFormatException exception) {
            return d;
        }
    }

    public static double percentage(double part, double total) {
        return percentage(part, total, 1);
    }

    public static double percentage(double part, double total, int degree) {
        return trim((part / total) * 100, degree);
    }

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Deprecated
    public static boolean parseInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException exception) {
        }
        return false;
    }

    public static double getKDR(int kills, int deaths) {
        if (kills == 0 || deaths == 0)
            return 0;
        return trim((double) kills / (double) deaths, 2);
    }

    public static String formatWithComas(int number) {
        String string = String.valueOf(number);
        if (string.length() <= 4)
            return string;
        for (int i = string.length() - 3; i > 0; i -= 3)
            string = string.substring(0, i) + "," + string.substring(i, string.length());
        return string;
    }

    public static int floor(double d1) {
        int i = (int) d1;
        return d1 >= i ? i : i - 1;
    }

    public static int d(float f1) {
        int i = (int) f1;
        return f1 >= i ? i : i - 1;
    }

    public static List<Block> getDiscreteCircle(Block centre, double radius) {
        List<Block> blocks = new ArrayList<>();
        double x = radius, y = 0, radiusSquared = radius * radius;
        while (x > y) {
            int x1 = (int) Math.round(x), y1 = (int) Math.round(y);
            blocks.add(centre.getWorld().getBlockAt(centre.getX() + x1, centre.getY(), centre.getZ() + y1));
            blocks.add(centre.getWorld().getBlockAt(centre.getX() - x1, centre.getY(), centre.getZ() + y1));
            blocks.add(centre.getWorld().getBlockAt(centre.getX() + x1, centre.getY(), centre.getZ() - y1));
            blocks.add(centre.getWorld().getBlockAt(centre.getX() - x1, centre.getY(), centre.getZ() - y1));
            blocks.add(centre.getWorld().getBlockAt(centre.getX() + y1, centre.getY(), centre.getZ() + x1));
            blocks.add(centre.getWorld().getBlockAt(centre.getX() - y1, centre.getY(), centre.getZ() + x1));
            blocks.add(centre.getWorld().getBlockAt(centre.getX() + y1, centre.getY(), centre.getZ() - x1));
            blocks.add(centre.getWorld().getBlockAt(centre.getX() - y1, centre.getY(), centre.getZ() - x1));
            x = Math.sqrt(x * x - 2 * y - 1);
            y = Math.sqrt(radiusSquared - x * x);
        }
        return blocks;
    }

    //Use this to sort a map by it's value. 
    public static <K, V extends Comparable<? super V>> List<Entry<K, V>> sortEntriesByValue(Map<K, V> map) {
        List<Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return sortedEntries;
    }

    public static int getRandom(int lower, int upper) {
        Random random = new Random();
        return random.nextInt((upper - lower) + 1) + lower;
    }
}