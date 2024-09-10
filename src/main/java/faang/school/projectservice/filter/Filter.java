package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter<F, E> {
    boolean isApplicable(F filter);

    Stream<E> apply(F filter, Stream<E> entityList);
}
