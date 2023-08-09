package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class MomentDateFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getDatePattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment -> moment.getDate().getMonthValue() == filters.getDatePattern().getMonthValue());
    }
}
