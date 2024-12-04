package faang.school.projectservice.utils;

import java.util.List;

public class CollectionUtils {

    public static <T> boolean isNotEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }
}
