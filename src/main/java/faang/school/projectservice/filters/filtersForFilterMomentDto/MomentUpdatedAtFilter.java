package faang.school.projectservice.filters.filtersForFilterMomentDto;

import faang.school.projectservice.filters.FilterMomentDto;
import faang.school.projectservice.filters.MomentFilter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentUpdatedAtFilter implements MomentFilter {
    @Override
    public boolean isApplicable(FilterMomentDto filterMomentDto) {
        return filterMomentDto.getUpdatedTimePattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto) {
        return moments.filter(moment -> moment.getUpdatedAt().isAfter(filterDto.getUpdatedTimePattern()));
    }
}
