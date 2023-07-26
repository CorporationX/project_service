package faang.school.projectservice.filters.moments.filtersForFilterMomentDto;

import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.filters.moments.MomentFilter;
import faang.school.projectservice.controller.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(FilterMomentDto filterMomentDto) {
        return filterMomentDto.getDatePattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto) {
        return moments.filter(moment -> moment.getDate().isAfter(filterDto.getDatePattern()));
    }
}
