package faang.school.projectservice.service.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.List;
import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filters);

    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters);
}
