package faang.school.projectservice.filters;

import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface FiltersDto {
    boolean isApplicable(FilterMomentDto filterMomentDto);

    Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto);
}
