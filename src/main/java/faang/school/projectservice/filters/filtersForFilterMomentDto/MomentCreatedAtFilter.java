package faang.school.projectservice.filters.filtersForFilterMomentDto;

import faang.school.projectservice.filters.FilterMomentDto;
import faang.school.projectservice.filters.FiltersDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public class MomentCreatedAtFilter implements FiltersDto {
    @Override
    public boolean isApplicable(FilterMomentDto filterMomentDto) {
        return filterMomentDto.getCreatedTimePattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto) {
        return moments.filter(moment -> moment.getCreatedAt().isAfter(filterDto.getCreatedTimePattern()));
    }
}
