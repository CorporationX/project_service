package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.List;
import java.util.stream.Stream;

public interface MomentFilter {
    Boolean isApplicable(MomentFilterDto momentFilterDto);

    Stream<Moment> apply(MomentFilterDto momentFilterDto, Stream<Moment> moments);
}
