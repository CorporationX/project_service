package faang.school.projectservice.filters.moments;

import faang.school.projectservice.controller.model.Moment;
import faang.school.projectservice.filters.moments.FilterMomentDto;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(FilterMomentDto filterMomentDto);

    Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto);
}
