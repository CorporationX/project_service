package faang.school.projectservice.filter.moment.filtersForFilterMomentDto;

import faang.school.projectservice.filter.moment.FilterMomentDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentCreatedAtFilter implements MomentFilter {
    @Override
    public boolean isApplicable(FilterMomentDto filterMomentDto) {
        return filterMomentDto.getCreatedTimePattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto) {
        return moments.filter(moment -> moment.getCreatedAt().isAfter(filterDto.getCreatedTimePattern()));
    }
}
