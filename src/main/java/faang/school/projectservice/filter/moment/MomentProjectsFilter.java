package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MomentProjectsFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return Objects.nonNull(momentFilterDto.getProjectIds()) && !momentFilterDto.getProjectIds().isEmpty();
    }

    @Override
    public void apply(List<Moment> moments, MomentFilterDto momentFilterDto) {
         moments.removeIf(moment -> !moment.getProjects().stream()
                .map(Project::getId)
                .collect(Collectors.toSet()).containsAll(momentFilterDto.getProjectIds()));
    }
}
