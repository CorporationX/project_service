package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Data
@NoArgsConstructor
@Component
public class MomentDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.getStartDate() != null && filterDto.getEndDate() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentStream, MomentFilterDto filterDto) {
        return momentStream.filter(moment -> moment.getDate().isAfter(filterDto.getStartDate()) &&
                moment.getDate().isBefore(filterDto.getEndDate()));
    }
}
