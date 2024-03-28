package faang.school.projectservice.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto momentFilterDto);
    Stream<Moment> apply(Stream<Moment> momentStream, MomentFilterDto momentFilterDto);
}
