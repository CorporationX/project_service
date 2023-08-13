package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final ProjectService projectService;
    private final UserServiceClient userServiceClient;

    public void validateFilter(SubprojectFilterDto subprojectFilterDto) {
        validateProjectId(subprojectFilterDto.getId());
        validateOwnerId(subprojectFilterDto.getRequesterId());
    }

    private void validateProjectId(Long id) {
        validateId(id);
        if (projectService.isExistProjectById(id)) {
            throw new DataValidationException("Can't be exist two project with equals id");
        }
    }

    private void validateOwnerId(Long ownerId) {
        validateId(ownerId);
        userServiceClient.getUser(ownerId);
    }

    private void validateId(Long id) {
        if (id == null || id < 0) {
            throw new DataValidationException("It's wrong id, id can't be null");
        }
    }
}
