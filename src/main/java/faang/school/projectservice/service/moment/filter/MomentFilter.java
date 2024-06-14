package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.List;
import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto momentFilterDto);

    Stream<Moment> apply(List<Moment> momentList, MomentFilterDto momentFilterDto);
}