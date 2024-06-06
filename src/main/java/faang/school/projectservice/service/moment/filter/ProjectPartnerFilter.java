package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;

import java.util.HashSet;
import java.util.stream.Stream;

public class ProjectPartnerFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters.getProjectIds() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment ->
                new HashSet<>(moment.getProjects()
                        .stream()
                        .map(Project::getId)
                        .toList()
                ).containsAll(filters.getProjectIds())
        );
    }
}
