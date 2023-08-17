package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentProjectsFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getProjectsPattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment -> filters.getProjectsPattern()
                .containsAll(moment.getProjects()
                        .stream()
                        .map(Project::getId)
                        .toList()));
    }
}
