package faang.school.projectservice.filter.moment;

import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(FilterMomentDto filterMomentDto);

    Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto);
}
