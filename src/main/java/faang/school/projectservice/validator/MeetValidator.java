package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.model.dto.UserDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MeetValidator {
    private final UserServiceClient userServiceClient;

    public void validateUser(long userId) {
        if (userId <= 0) {
            throw new DataValidationException(String.format("User's id can't be equal %d", userId));
        }
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            throw new DataValidationException(String.format("The user must exist in the system, userId = %d", userId));
        }
    }

    public void validateUserIsCreator(long userId, long creatorId) {
        if (userId != creatorId) {
            throw new DataValidationException("The meeting creator's ID does not match the user's ID");
        }
    }

    public void validateIdFromPath(long id, Long meetDtoId) {
        if (meetDtoId != null && meetDtoId != id) {
            throw new DataValidationException("The ID in the path must match the ID in the DTO");
        }
    }
}
