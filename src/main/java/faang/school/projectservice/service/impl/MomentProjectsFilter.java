package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.MomentFilter;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class MomentProjectsFilter implements MomentFilter {
    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.projectsIds() != null && !filter.projectsIds().isEmpty();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filter) {
        Predicate<Moment> momentPredicate =
                moment -> moment.getProjects().stream()
                        .map(Project::getId).toList()
                        .containsAll(filter.projectsIds());
        return moments.filter(momentPredicate);
    }
}
