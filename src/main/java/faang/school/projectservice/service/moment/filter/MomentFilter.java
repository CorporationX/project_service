package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter extends Filter<Moment, MomentFilterDto> {
    boolean isApplicable(MomentFilterDto filters);
    Stream<Moment> getApplicableFilters(Stream<Moment> moments, MomentFilterDto filters);
}
