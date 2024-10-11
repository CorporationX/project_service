package faang.school.projectservice.model.filter.moment;

import faang.school.projectservice.model.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.entity.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filters);

    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters);
}
