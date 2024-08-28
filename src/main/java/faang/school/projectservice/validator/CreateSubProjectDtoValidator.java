package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateSubProjectDtoValidator {
    private final ProjectRepository projectRepository;

    public void validateOnCreate(CreateSubProjectDto projectDto) {
        validateVisibility(projectDto);
        validateIsParentProjectExist(projectDto);
    }

    public void validateOnUpdate(UpdateSubProjectDto projectDto) {
        validateIsProjectExist(projectDto);
        validateIsSubProjectsHaveSameStatus(projectDto);
    }

    private void validateVisibility(CreateSubProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getParentId());
        if (project.getVisibility().equals(ProjectVisibility.PUBLIC)
                && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new ValidationException("Project visibility is public");
        }
    }

    private void validateIsParentProjectExist(CreateSubProjectDto projectDto) {
        if (!projectRepository.existsById(projectDto.getParentId())) {
            throw new ValidationException("Parent project does not exist");
        }

    }

    private void validateIsProjectExist(UpdateSubProjectDto projectDto) {
        if (!projectRepository.existsById(projectDto.getId())) {
            throw new ValidationException("Project does not exist");
        }

    }

    private void validateIsSubProjectsHaveSameStatus(UpdateSubProjectDto projectDto) {
        List<String> projectNamesWithWrongStatus = projectRepository.getProjectById(projectDto.getId())
                .getChildren()
                .stream()
                .filter(p -> !p.getStatus().equals(projectDto.getStatus()))
                .map(Project::getName)
                .toList();

        if (!projectNamesWithWrongStatus.isEmpty()) {
            throw new ValidationException("Sub projects with names: " + projectNamesWithWrongStatus +
                    " do not have the same status as the parent project");
        }
    }

}
