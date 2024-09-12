package faang.school.projectservice.filter.moment;

import faang.school.projectservice.model.Moment;

import java.util.List;

public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filterDto);

    List<Moment> apply(MomentFilterDto filterDto, List<Moment> moments);


}
