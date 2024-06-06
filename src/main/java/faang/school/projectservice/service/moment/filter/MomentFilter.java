package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filters);
    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters);
}