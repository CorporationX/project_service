package faang.school.projectservice.filter;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MomentAfterDateFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getAfterDateFilter() != null;
    }

    @Override
    public List<Moment> apply(List<Moment> moments, MomentFilterDto momentFilterDto) {
        return moments.stream()
                .filter(moment -> moment.getDate().isAfter(momentFilterDto.getAfterDateFilter()))
                .toList();
    }
}
