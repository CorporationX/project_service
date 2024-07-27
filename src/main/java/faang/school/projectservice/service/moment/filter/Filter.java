package faang.school.projectservice.service.moment.filter;

import java.util.stream.Stream;

public interface Filter<T, F> {
    boolean isApplicable(F filter);
    Stream<T> getApplicableFilters(Stream<T> stream, F filter);
}
