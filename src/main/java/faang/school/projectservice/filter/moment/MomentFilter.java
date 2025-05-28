package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentDto momentDto);

    Stream<Moment> apply(Stream<Moment> moments, MomentDto momentDto);
}
