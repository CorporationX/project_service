package faang.school.projectservice.filters.projectFilters;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validator.projectservice.ProjectParticipantValidatorByVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FilterByProjectVisibility implements ProjectFilter {

    private final ProjectParticipantValidatorByVisibility visibilityValidator;
    private final UserContext userContext;

    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getVisibility() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto) {
        Long currentUserId = userContext.getUserId();

        return projects.filter(project ->
                project.getVisibility() == filterDto.getVisibility() ||
                        (project.getVisibility() == ProjectVisibility.PRIVATE && visibilityValidator.isUserParticipantInProject(project, currentUserId))
        );
    }
}


