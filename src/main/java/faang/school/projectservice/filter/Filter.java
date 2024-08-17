package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.FilterDto;

import java.util.stream.Stream;

public interface Filter<T extends FilterDto, F> {
    boolean isApplicable(T filterDto);

    Stream<F> apply(Stream<F> itemStream, T filterDto);
}
