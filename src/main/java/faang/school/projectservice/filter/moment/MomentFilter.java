package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;

import java.util.stream.Stream;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto momentFilterDto);

    void apply(Stream<MomentDto> moments, MomentFilterDto momentFilterDto);
}
