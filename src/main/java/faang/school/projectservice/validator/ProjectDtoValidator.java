package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectDtoValidator {

    private final ProjectRepository projectRepository;

    public void validateIfProjectNameOrDescriptionIsBlank(ProjectDto projectDto) {
        if (projectDto.getName().isBlank() || projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Field name or description can not be empty");
        }
    }

    public void validateIfOwnerAlreadyExistProjectWithName(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Owner already has a project with name " + projectDto.getName());
        }
    }

    public void validateIfProjectIsExistInDb(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new DataValidationException("Project with " + id + "does not exist");
        }
    }

    public void validateIfDtoContainsExistedProjectStatus(ProjectStatus status) {
        List<ProjectStatus> statuses = Arrays.asList(ProjectStatus.values());
        if (!statuses.contains(status)) {
            throw new DataValidationException("ProjectStatus " + status + "does not exist");
        }
    }

    public void validateIfProjectDescriptionAndStatusIsBlank(ProjectDto projectDto) {
        if (projectDto.getName().isBlank() && projectDto.getDescription().isBlank()) {
            throw new DataValidationException("Field name and description can not be empty");
        }
    }
}
