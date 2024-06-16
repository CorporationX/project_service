package faang.school.projectservice.validation.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidatorImpl implements ProjectValidator {
    private final ProjectRepository projectRepository;

    @Override
    public void validateProjectByOwnerIdAndNameOfProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("The user with id: "+projectDto.getOwnerId()+ " already has a project with name "+projectDto.getName());
        }
    }

    @Override
    public void validateProjectExistence(long id) {
        boolean exists = projectRepository.existsById(id);
        if (!exists) {
            String message = String.format("a project with id %d does not exist", id);

            throw new DataValidationException(message);
        }
    }
}
