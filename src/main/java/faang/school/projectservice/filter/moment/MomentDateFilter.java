package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@NoArgsConstructor
class MomentDateFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getStartDate() != null || momentFilterDto.getEndDate() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentList, MomentFilterDto momentFilterDto) {
        Optional<LocalDateTime> startDate = Optional.ofNullable(momentFilterDto.getStartDate());
        Optional<LocalDateTime> endDate = Optional.ofNullable(momentFilterDto.getEndDate());

        return filter(momentList, startDate, endDate);
    }

    private Stream<Moment> filter(Stream<Moment> moments, Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate) {
        return moments
                .filter(moment -> startDate.map(start -> moment.getDate().isAfter(start)).orElse(true))
                .filter(moment -> endDate.map(end -> moment.getDate().isBefore(end)).orElse(true));
    }
}
