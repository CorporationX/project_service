package faang.school.projectservice.filter;


import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public interface SubProjectFilter<T, F> {
    boolean isApplicable(T filter);

    Stream<F> apply(Stream<F> itemStream, T filter);
}
