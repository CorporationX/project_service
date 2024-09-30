package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.MeetValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MeetValidator {
    private final UserServiceClient userServiceClient;
    public void validateUser(Long userId) {
        userServiceClient.getUser(userId);
    }

    public void validateEditPermission(Long userId, Long creatorId) {
        userServiceClient.getUser(userId);
        if (!userId.equals(creatorId)) {
            throw new MeetValidationException("You cannot update not yours meets");
        }
    }

    public void validateParticipants(List<Long> userIds) {
        var foundUserIds = userServiceClient.getUsersByIds(userIds).stream()
                .map(UserDto::getId)
                .toList();
        if (!(userIds.containsAll(foundUserIds) && foundUserIds.containsAll(userIds))) {
            throw new MeetValidationException("Wrong list of userIds");
        }
    }
}
