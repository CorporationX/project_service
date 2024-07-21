package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectServiceValidator {
    private final ProjectRepository projectRepository;
    public void validateExistingId(Long id) {
        if (id == null) {
            throw new DataValidationException("project ID can't be null");
        } else if (!projectRepository.existsById(id)) {
            throw new DataValidationException("project with this ID: " + id + "doesn't exist");
        }
    }

    public void validateSubProjectVisibility(CreateSubProjectDto subProjectDto) {
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentProjectId());
        if (parentProject.getVisibility() != subProjectDto.getVisibility()) {
            throw new DataValidationException("Visibility of parent project and subproject should be equals");
        } else if (projectRepository.existsById(subProjectDto.getId())) {
            throw new DataValidationException("project with this ID: " + subProjectDto.getId() + "already exists");
        }
    }
}
