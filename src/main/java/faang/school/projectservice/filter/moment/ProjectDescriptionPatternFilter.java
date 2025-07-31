package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectDescriptionPatternFilter implements MomentFilter {
    @Override
    public Boolean isApplicable(MomentFilterDto momentFilterDto) {
        return momentFilterDto.getProjectNamePattern() != null;
    }

    @Override
    public Stream<Moment> apply(MomentFilterDto momentFilterDto, Stream<Moment> moments) {
        String projectDescriptionPattern = momentFilterDto.getProjectDescriptionPattern();

        return moments
                .filter(moment -> moment.getProjects().stream()
                        .anyMatch(project ->
                                project.getDescription().matches(".*" + projectDescriptionPattern + ".*")));
    }
}
