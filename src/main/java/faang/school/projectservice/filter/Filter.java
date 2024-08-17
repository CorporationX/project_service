package faang.school.projectservice.filter;

import java.util.stream.Stream;

public interface Filter<F, D> {
    boolean isApplicable(F filters);

    Stream<D> apply(Stream<D> vacancies, F filters);
}
