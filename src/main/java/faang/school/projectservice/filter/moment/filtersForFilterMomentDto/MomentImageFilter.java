package faang.school.projectservice.filter.moment.filtersForFilterMomentDto;

import faang.school.projectservice.filter.moment.FilterMomentDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentImageFilter implements MomentFilter {
    @Override
    public boolean isApplicable(FilterMomentDto filterMomentDto) {
        return filterMomentDto.getImagePattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto) {
        return moments.filter(moment -> moment.getImageId().contains(filterDto.getImagePattern()));
    }
}
