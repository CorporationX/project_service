package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class MomentDateFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return true;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        LocalDateTime dateFrom = Optional.ofNullable(filter.dateFrom()).orElse(LocalDateTime.MIN);
        LocalDateTime dateTo = Optional.ofNullable(filter.dateTo()).orElse(LocalDateTime.MAX);
        return moments
                .filter(moment -> moment.getDate().isAfter(dateFrom) && moment.getDate().isBefore(dateTo));
    }
}
