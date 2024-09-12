package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MomentFilterMonth implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.month() != null;
    }

    @Override
    public List<Moment> apply(MomentFilterDto filterDto, List<Moment> moments) {
        return moments.stream()
                .filter(moment -> moment.getDate().getMonth() == filterDto.month())
                .toList();
    }

}
