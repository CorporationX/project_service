package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;

import java.util.Objects;
import java.util.stream.Stream;

public class MomentMonthFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return Objects.nonNull(momentFilterDto.getMonth());
    }

    @Override
    public void apply(Stream<MomentDto> moments, MomentFilterDto momentFilterDto) {

    }
}
