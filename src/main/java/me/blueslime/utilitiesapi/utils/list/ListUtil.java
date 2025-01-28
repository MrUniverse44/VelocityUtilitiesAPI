package me.blueslime.utilitiesapi.utils.list;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListUtil {

    @SafeVarargs
    public static <E> List<E> createWith(ListType type, Collection<E>... elements) {
        List<E> elementsToAdd = new ArrayList<>();

        for (Collection<E> element : elements) {
            elementsToAdd.addAll(element);
        }

        return createWith(type, elementsToAdd);
    }

    @SafeVarargs
    public static <E> List<E> createWith(ListType type, E... elements) {
        return createWith(type, Arrays.asList(elements));
    }

    public static <E> List<E> createWith(ListType type, Collection<E> elements) {
        switch (type) {
            case LINKED:
                return new LinkedList<>(elements);
            case ARRAY:
                return new ArrayList<>(elements);
            default:
                return new CopyOnWriteArrayList<>(elements);
        }
    }

    @SafeVarargs
    public static <E> Set<E> toSet(Collection<E>... elements) {
        Set<E> elementSet = new HashSet<>();

        for (Collection<E> element : elements) {
            elementSet.addAll(element);
        }

        return elementSet;
    }

    @SafeVarargs
    public static <E> Set<E> toSet(E... elements) {
        return toSet(Arrays.asList(elements));
    }
}
