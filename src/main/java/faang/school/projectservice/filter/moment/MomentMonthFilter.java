package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MomentMonthFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return Objects.nonNull(momentFilterDto.getMonth());
    }

    @Override
    public void apply(List<Moment> moments, MomentFilterDto momentFilterDto) {
        moments.removeIf(m -> m.getDate().getMonthValue() != momentFilterDto.getMonth());
    }
}
