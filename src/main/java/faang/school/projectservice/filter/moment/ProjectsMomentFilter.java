package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ProjectsMomentFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.projectIds() != null;
    }

    @Override
    public Stream<Moment> apply(MomentFilterDto filterDto, Stream<Moment> moments) {
        return moments.filter(moment -> {
                    Set<Long> projectIds = moment.getProjects().stream()
                            .map(Project::getId)
                            .collect(Collectors.toSet());
                    return projectIds.containsAll(filterDto.projectIds());
                });
    }
}
