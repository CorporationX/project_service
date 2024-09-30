package faang.school.projectservice.validator.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void validateProject(ProjectDto projectDto) {
        validateOwnerHasSameProject(projectDto);
    }

    public void validateUpdatedFields(ProjectDto projectDto) {
        if ((projectDto.getStatus() == null)) {
            throw new DataValidationException("At least one updated field must not be empty");
        }
    }

    private void validateOwnerHasSameProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Owner already has a project with name " + projectDto.getName());
        }
    }
}