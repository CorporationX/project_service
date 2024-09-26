package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.filter.moment.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Moment;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@NoArgsConstructor
class MomentDateFilter implements Filter<MomentFilterDto, Moment> {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getStartDate() != null || momentFilterDto.getEndDate() != null;
    }

    @Override
    public Stream<Moment> applyFilter(Stream<Moment> momentList, MomentFilterDto momentFilterDto) {
        Optional<LocalDateTime> startDate = Optional.ofNullable(momentFilterDto.getStartDate());
        Optional<LocalDateTime> endDate = Optional.ofNullable(momentFilterDto.getEndDate());

        return momentList
                .filter(moment -> startDate.map(start -> moment.getDate().isAfter(start)).orElse(true))
                .filter(moment -> endDate.map(end -> moment.getDate().isBefore(end)).orElse(true));
    }
}
