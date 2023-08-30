package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final UserServiceClient userServiceClient;

    public void validateFilter(SubprojectFilterDto subprojectFilterDto) {
        validateOwnerId(subprojectFilterDto.getRequesterId());
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
