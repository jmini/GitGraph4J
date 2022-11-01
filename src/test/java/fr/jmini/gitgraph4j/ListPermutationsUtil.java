package fr.jmini.gitgraph4j;

import java.util.ArrayList;
import java.util.List;

public class ListPermutationsUtil {

    private ListPermutationsUtil() {
    }

    public static <T> List<List<T>> computePermutations(List<T> list) {
        return addOneRemaining(List.of(), list, null);
    }

    public static <T> List<List<T>> computePermutations(List<T> list, Integer limit) {
        return addOneRemaining(List.of(), list, limit);
    }

    private static <T> List<List<T>> addOneRemaining(List<List<T>> list, List<T> remaining, Integer limit) {
        if (remaining.isEmpty()) {
            return list;
        }
        int size;
        if (list.isEmpty()) {
            size = 0;
        } else {
            size = list.get(0)
                    .size();
        }
        if (limit != null && size >= limit) {
            return list;
        }
        List<List<T>> r = new ArrayList<>();
        for (T e : remaining) {
            List<T> newRemaining = remaining.stream()
                    .filter(i -> i != e)
                    .toList();

            List<List<T>> newList;
            if (list.isEmpty()) {
                newList = List.of(List.of(e));
            } else {
                newList = list.stream()
                        .map(l -> {
                            List<T> n = new ArrayList<>();
                            n.addAll(l);
                            n.add(e);
                            return n;
                        })
                        .toList();
            }
            r.addAll(addOneRemaining(newList, newRemaining, limit));
        }
        return r;
    }
}
