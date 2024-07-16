package faang.school.projectservice.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionsUtil {

    // Возвращает список элементов из левой коллекции, которые не относятся к правой коллекции
    public static <T> List<T> leftOuterJoin(Collection<T> leftCollection, Collection<T> rightCollection) {
        Set<T> rightCollectionSet = new HashSet<>(rightCollection);
        return leftCollection.stream()
                .filter(leftItem -> !rightCollectionSet.contains(leftItem))
                .toList();
    }
}
