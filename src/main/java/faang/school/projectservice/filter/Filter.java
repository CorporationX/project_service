package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter<R, U> {
    boolean isApplicable (U u);
    Stream<R> apply(Stream<R> r, U u);
}