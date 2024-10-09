package faang.school.projectservice.model.filter.moment;

import faang.school.projectservice.model.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.entity.Moment;

import java.util.stream.Stream;

public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters.projectId() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment -> moment.getProjects()
                .stream()
                .anyMatch(project -> filters.projectId()
                        .contains(project.getId())));
    }
}
