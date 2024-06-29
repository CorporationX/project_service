package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@Component
class MomentDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getStartDate() != null || momentFilterDto.getEndDate() != null;
    }

    @Override
    public Stream<Moment> apply(List<Moment> momentList, MomentFilterDto momentFilterDto) {
        return momentList.stream().filter(
                moment -> moment.getDate().isAfter(momentFilterDto.getStartDate()) &&
                        moment.getDate().isBefore(momentFilterDto.getEndDate())
        );
    }
}
