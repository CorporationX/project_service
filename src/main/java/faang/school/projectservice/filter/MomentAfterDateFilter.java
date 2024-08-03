package faang.school.projectservice.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MomentAfterDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getAfterDateFilter() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto momentFilterDto) {
        return moments
                .filter(moment -> moment.getDate().isAfter(momentFilterDto.getAfterDateFilter()));
    }
}
