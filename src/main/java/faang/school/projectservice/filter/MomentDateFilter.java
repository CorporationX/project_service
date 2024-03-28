package faang.school.projectservice.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getStartDate() != null && momentFilterDto.getEndDate() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentStream, MomentFilterDto momentFilterDto) {
        return momentStream.filter(moment -> moment.getDate().isAfter(momentFilterDto.getStartDate()) &&
                moment.getDate().isBefore(momentFilterDto.getEndDate()));
    }
}
