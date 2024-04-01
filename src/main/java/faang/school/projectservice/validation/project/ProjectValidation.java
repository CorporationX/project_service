package faang.school.projectservice.validation.project;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.handler.exceptions.DataValidationException;
import faang.school.projectservice.handler.exceptions.EntityNotFoundException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidation {

    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;

    public void validationCreate(ProjectDto projectDto) {
        validateDtoFields(projectDto);
        validateProjectNameUnique(projectDto);
        validateUserExistsById(projectDto);
    }

    public void validationUpdate(ProjectDto projectDto) {
        projectExistsById(projectDto.getId());
        validateDtoFields(projectDto);
    }

    private void validateDtoFields(ProjectDto projectDto) {
        validateTitle(projectDto);
        validateDescription(projectDto);
        validateProjectVisibility(projectDto);
    }

    public void projectExistsById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException(ProjectConstraints.PROJECT_NOT_EXIST.getMessage());
        }
    }

    private void validateTitle(ProjectDto projectDto) {
        if (projectDto.getName() == null) {
            throw new DataValidationException(ProjectConstraints.PROJECT_NAME_CAN_NOT_BE_NULL.getMessage());
        }
        if (projectDto.getName().isBlank()) {
            throw new DataValidationException(ProjectConstraints.PROJECT_NAME_CAN_NOT_BE_BLANK.getMessage());
        }
    }

    private void validateDescription(ProjectDto projectDto) {
        if (projectDto.getDescription() == null) {
            throw new DataValidationException(ProjectConstraints.PROJECT_DESCRIPTION_CAN_NOT_BE_NULL.getMessage());
        }
        if (projectDto.getDescription().isBlank()) {
            throw new DataValidationException(ProjectConstraints.PROJECT_DESCRIPTION_CAN_NOT_BE_BLANK.getMessage());
        }
    }

    private void validateProjectVisibility(ProjectDto projectDto) {
        if (projectDto.getVisibility() == null) {
            throw new DataValidationException(ProjectConstraints.PROJECT_VISIBILITY_CAN_NOT_BE_NULL.getMessage());
        }
    }

    private void validateProjectNameUnique(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(ProjectConstraints.PROJECT_NAME_MUST_BE_UNIQUE.getMessage());
        }
    }

    private void validateUserExistsById(ProjectDto projectDto) {
        if(userServiceClient.getUser(projectDto.getOwnerId()) == null){
            throw new EntityNotFoundException("User with id " + projectDto.getOwnerId() + " not found in database");
        }
    }
}
