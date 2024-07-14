package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.List;
import java.util.stream.Stream;

public interface MomentFilter {

    boolean isApplicable(MomentFilterDto filter);

    Stream<Moment> apply(List<Moment> moments, MomentFilterDto filter);
}
