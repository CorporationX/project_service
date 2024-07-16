package faang.school.projectservice.service.filter;

import java.util.stream.Stream;

public interface Filter<T, F> {
    boolean isApplicable(F FilterDto);
    Stream<T> apply(Stream<T> items, F FilterDto);
}
