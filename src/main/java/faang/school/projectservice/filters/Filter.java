package faang.school.projectservice.filters;

import java.util.stream.Stream;

public interface Filter<D, E> {

     boolean isApplicable(D filterDto);

     Stream<E> apply(Stream<E> entityStream, D filterDto);
}
