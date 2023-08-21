package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MomentFilter {
    boolean isApplicable(MomentFilterDto momentFilterDto);

    void apply(List<Moment> moments, MomentFilterDto momentFilterDto);
}
