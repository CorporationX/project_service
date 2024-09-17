package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.List;
import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filterDto);

    Stream<Moment> apply(MomentFilterDto filterDto, Stream<Moment> moments);


}
