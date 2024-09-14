package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;

import java.util.stream.Stream;

public class MomentProjectFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return !filters.getProjectId().isEmpty();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment -> moment.getProjects()
                .stream()
                .anyMatch(project -> filters.getProjectId()
                        .contains(project.getId())));
    }
}
