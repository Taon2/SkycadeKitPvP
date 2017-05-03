package net.skycade.kitpvp.coreclasses.algorithms;

import java.util.Iterator;

// Not the best, O(n) performance
public class SequentialSearch {

    // Returns -1 if not found
    public static <T> int searchIndex(T[] array, T searchKey) {
        for (int i = 0; i < array.length; i++)
            if (array[i].equals(searchKey))
                return i;
        return -1;
    }

    // Returns -1 if not found
    public static <T> int searchIndex(Iterable<T> collection, T searchKey) {
        Iterator<T> iter = collection.iterator();
        int i = 0;
        while (iter.hasNext()) {
            if (iter.next().equals(searchKey)) {
                return i;
            }
            i++;
        }
        return -1;
    }

}