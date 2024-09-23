package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.project.ProjectServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ValidatorProject {
    private final ProjectService projectService;

    public void validationName(ProjectDto projectDto) {
        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new NoSuchElementException("Need project name");
        }
    }

    public void validationDescription(ProjectDto projectDto) {
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new NoSuchElementException("Need project description");
        }
    }

    public void validationCreateProject(ProjectDto projectDto) {
        validationName(projectDto);
        validationDescription(projectDto);
        projectService.validationDuplicateProjectNames(projectDto);
    }
}
