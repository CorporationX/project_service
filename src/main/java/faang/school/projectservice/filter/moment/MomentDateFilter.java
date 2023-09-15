package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getDate() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentsStream, MomentFilterDto filter) {
        return momentsStream.filter(moment -> moment.getDate().getMonthValue() == filter.getDate().getMonthValue());
    }
}
