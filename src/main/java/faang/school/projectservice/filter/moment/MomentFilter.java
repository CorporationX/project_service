package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.List;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filter);

    void apply(List<Moment> moments, MomentFilterDto filter);
}
