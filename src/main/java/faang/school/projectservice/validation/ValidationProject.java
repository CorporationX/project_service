package faang.school.projectservice.validation;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ValidationProject {
    private final ProjectMapper mapper;
    private final ProjectRepository projectRepository;

    public Project getEntity(ProjectDto projectDto) {
        return mapper.toEntity(projectDto);
    }

    public void validationName(ProjectDto projectDto) {
        Project project = getEntity(projectDto);

        if (project.getName() == null || project.getName().isBlank()) {
            throw new NoSuchElementException("Need project name");
        }
    }

    public void validationDescription(ProjectDto projectDto) {
        Project project = getEntity(projectDto);

        if (project.getDescription() == null || project.getDescription().isBlank()) {
            throw new NoSuchElementException("Need project description");
        }
    }

    public void validationDuplicateProjectNames(ProjectDto projectDto) {
        Project project = getEntity(projectDto);
        Project existingProject = projectRepository.findProjectByNameAndOwnerId(projectDto.getName(),
                project.getOwnerId());

        if (existingProject != null && !existingProject.getId().equals(project.getId())) {
            throw new NoSuchElementException("This user already has a project with this name");
        }
    }

    public void validationCreateProject(ProjectDto projectDto) {
        validationName(projectDto);
        validationDescription(projectDto);
        validationDuplicateProjectNames(projectDto);
        validationDuplicateProjectNames(projectDto);
    }
}
