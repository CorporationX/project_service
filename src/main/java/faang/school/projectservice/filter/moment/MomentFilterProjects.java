package faang.school.projectservice.filter.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class MomentFilterProjects implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filterDto) {
        return filterDto.projectIds() != null;
    }

    @Override
    public List<Moment> apply(MomentFilterDto filterDto, List<Moment> moments) {
        return moments.stream()
                .filter(moment -> {
                    List<Long> projectIds = moment.getProjects().stream()
                            .map(Project::getId)
                            .toList();
                    return new HashSet<>(projectIds).containsAll(filterDto.projectIds());
                })
                .toList();
    }
}
