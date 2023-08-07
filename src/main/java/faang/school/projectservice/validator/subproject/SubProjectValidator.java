package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.subproject.SubProjectCreateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.subproject.SubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final SubProjectService subProjectService;
    private final UserServiceClient userServiceClient;
    private final String NAME="Name";
    private final String DESCRIPTION="Description";

    public void validateCreateProjectDto(SubProjectCreateDto subProjectCreateDto) {
        validateRequiredFields(subProjectCreateDto.getName(),NAME);
        validateRequiredFields(subProjectCreateDto.getDescription(),DESCRIPTION);

        validateOwnerId(subProjectCreateDto.getOwnerId());
        validateParentProject(subProjectCreateDto.getParentProjectId());
    }

    private void validateOwnerId(Long ownerId) {
        validateId(ownerId);
        userServiceClient.getUser(ownerId);
    }

    private void validateParentProject(Long projectId) {
        validateId(projectId);
        subProjectService.getProjectById(projectId);
    }

    private void validateRequiredFields(String fieldData, String message) {
        if (fieldData == null) {
            throw new DataValidationException(message+" can't be null");
        }

        int contentLength = fieldData.length();
        if (contentLength == 0 || contentLength >= 4096) {
            throw new DataValidationException("You wrote wrong"+ message+", pls revise it");
        }
    }

    private void validateId(Long id) {
        if (id == null || id < 0) {
            throw new DataValidationException("It's wrong id, id can't be null");
        }
    }
}
