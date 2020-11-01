package net.skycade.kitpvp.coreclasses.utils;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilString {

    public static String capitaliseString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++)
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'')
                found = false;
        return String.valueOf(chars);
    }

    public static String capitaliseFirstCharacter(String string) {
        if (string == null || string.length() == 0)
            return string;
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String serialiseList(List<String> l) {
        return serialiseList(l, ":");
    }

    public static String serialiseList(List<String> l, String delimiter) {
        String s = "";
        for (int i = 0; i < l.size(); i++)
            s += l.get(i) + (i == l.size() - 1 ? "" : delimiter);
        return s;
    }

    public static List<String> deserialiseList(String s) {
        return deserialiseList(s, ":");
    }

    public static List<String> deserialiseList(String s, String delimiter) {
        return new ArrayList<>(Arrays.asList(s.split(delimiter)));
    }

    public static boolean containsIgnoreCase(String text, String content) {
        return org.apache.commons.lang.StringUtils.containsIgnoreCase(text, content);
    }

    public static String replaceAllIgnoreCase(String text, String regex, String replacement) {
        return text.replaceAll("(?i)" + regex, replacement);
    }

    public static String removeBrackets(String text) {
        return text.replaceAll("[\\[\\](){}]", "");
    }

    public static String getWhitespace(int length) {
        String s = "";
        for (int i = 0; i < length; i++)
            s += " ";
        return s;
    }

    public static <T extends Enum<T>> T fromString(Class<T> enumeration, String name) {
        for (T value : enumeration.getEnumConstants())
            if (name.equalsIgnoreCase(value.name()))
                return value;
        throw new IllegalArgumentException("There is no value with name '" + name + " in Enum " + enumeration.getClass().getName());
    }

    /*
     * Never used
     * public static String centreForMinecraftChat(String s) {
     * int DEFAULT_CHAT_WIDTH = 106, RIGHT_MARGIN = 20;
     * int width = DEFAULT_CHAT_WIDTH - RIGHT_MARGIN;
     * return getWhitespace((width - ChatColor.stripColor(s).length()) / 2) + s;
     * }
     */

    public static Boolean getBoolean(String bool) {
        if (bool.equals("0") || bool.equalsIgnoreCase("false") || bool.equalsIgnoreCase("off") || bool.equalsIgnoreCase("no") || bool.equalsIgnoreCase("disable") || bool.equalsIgnoreCase("disabled"))
            return false;
        if (bool.equals("1") || bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("on") || bool.equalsIgnoreCase("yes") || bool.equalsIgnoreCase("enable") || bool.equalsIgnoreCase("enabled"))
            return true;
        return null;
    }

    public static Boolean booleanValueOf(String bool) {
        if (bool.equalsIgnoreCase("true") || bool.equals("1"))
            return true;
        if (bool.equalsIgnoreCase("false") || bool.equals("0"))
            return false;
        return null;
    }

    public static String serialiseMap(Map<String, String> map) {
        String s = "";
        for (Map.Entry<String, String> entry : map.entrySet())
            s += entry.getKey() + ":" + entry.getValue() + ",";
        s = s.substring(0, Math.max(0, s.length() - 1));
        return s;
    }

    public static Map<String, String> deserialiseMap(String string) {
        Map<String, String> map = new HashMap<>();
        for (String s : string.split(",")) {
            if (s.split(":").length != 2)
                continue;
            map.put(s.split(":")[0], s.split(":")[1]);
        }
        return map;
    }

    public static String removeRecurringSpaces(String string) {
        return string.trim().replaceAll(" +", " ");
    }

    public static String getWord(String message, int index) {
        if (index < 0 || index > message.length() - 1)
            return null;
        if (index == 0 || message.substring(index - 1, index).equals(" "))
            return "";
        String word = "";
        for (int i = index; i > 0; i--) {
            String character = message.substring(i - 1, i);
            if (character.equals(" "))
                break;
            word = character + word;
        }
        for (int i = index; i < message.length(); i++) {
            String character = message.substring(i, i + 1);
            if (character.equals(" "))
                break;
            word = word + character;
        }
        return word;
    }

    public static boolean isSimilar(String oldString, String newString, float matchRequirement) {
        if (newString.length() <= 3)
            return newString.toLowerCase().equals(oldString.toLowerCase());
        for (int i = 0; i < newString.length() * matchRequirement; i++) {
            int matchFromIndex = 0;
            // Look for substrings starting at i
            for (int j = 0; j < oldString.length(); j++) {
                // End of newString
                if (i + j >= newString.length())
                    break;
                // Matched
                if (newString.charAt(i + j) == oldString.charAt(j)) {
                    matchFromIndex++;
                    if (matchFromIndex >= newString.length() * matchRequirement)
                        return true;
                }
                // No Match > Reset
                else
                    break;
            }
        }
        return false;
    }

    public static List<String> formatOnLines(String string, int lineLength) {
        List<String> l = new ArrayList<>();
        int index = 0;
        String line = "";
        for (String s : string.split(" ")) {
            index += s.length();
            line += s + " ";
            if (index > lineLength) {
                l.add(line.trim());
                index = 0;
                line = "";
                continue;
            }
        }
        if (!line.isEmpty())
            l.add(line.trim());
        return l;
    }

    public static String concatinate(String delimiter, String... strings) {
        for (String s : strings)
            if (s.contains(delimiter))
                throw new IllegalArgumentException("String " + s + " contains delimiter " + delimiter + "!");
        String s = "";
        for (int i = 0; i < strings.length; i++)
            s += strings[i] + (i == strings.length - 1 ? "" : delimiter);
        return s;
    }

    public static boolean isIP(String ip) {
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4)
                return false;

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if (i < 0 || i > 255)
                    return false;
            }

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String joinArray(String[] array, int start, int stop) {
        String result = "";
        for (int i = start; i < stop; i++)
            result += array[i] + (i == stop - 1 ? "" : " ");
        return result;
    }

    public static int countMatches(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        int count = 0;

        while (matcher.find()) {
            count++;
        }

        return count;
    }

    public static List<String> findClosest(Collection<String> list, String startsWith) {
        List<String> closest = new ArrayList<>();

        for (String string : list) {
            if (string.toLowerCase().startsWith(startsWith.toLowerCase())) {
                closest.add(string);
            }
        }

        return closest;
    }

    public static String correctCase(Collection<String> list, String string) {
        for (String element : list) {
            if (element.equalsIgnoreCase(string)) {
                return element;
            }
        }
        return string;
    }

    public static String join(String... values) {
        return join(Lists.newArrayList(values));
    }

    public static String join(String[] values, String delimiter) {
        return join(Lists.newArrayList(values), delimiter);
    }

    public static String join(Collection<String> values) {
        return join(values, " ");
    }

    public static String join(Collection<String> values, String delimiter) {
        String join = "";
        for (String s : values)
            join += s + delimiter;
        return join.substring(0, Math.max(join.length() - delimiter.length(), 0));
    }

    /**
     * Useful for making A, B &amp; C.
     * For example, {@code join(Arrays.asList("A", "B", "C"), ", ", " & ")} will return "A, B &amp; C".
     *
     * @param values     the values to be joined
     * @param delimiter1 the standard delimiter to be used if not before last object
     * @param delimiter2 the delimiter to be used prior to last value
     * @return the joined string
     */
    public static String join(List<String> values, String delimiter1, String delimiter2) {
        String join = "";
        for (int i = 0; i < values.size(); i++)
            join += values.get(i) + (i == values.size() - 1 ? "" : i == values.size() - 2 ? delimiter2 : delimiter1);
        return join;
    }

    public static List<String> split(String string, int length) {
        List<String> l = Lists.newArrayList();
        String line = "";
        for (String word : string.split(" ")) {
            line += word + " ";
            if (line.trim().length() >= length) {
                l.add(line.trim());
                line = "";
            }
        }
        line = line.trim();
        if (!line.isEmpty())
            l.add(line);
        return l;
    }

}