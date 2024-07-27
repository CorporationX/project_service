package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class MomentNameFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<Moment> getApplicableFilters(Stream<Moment> moments, MomentFilterDto filters) {
        return moments
                .filter(moment -> moment.getName().toLowerCase().contains(filters.getNamePattern().toLowerCase()));
    }
}
