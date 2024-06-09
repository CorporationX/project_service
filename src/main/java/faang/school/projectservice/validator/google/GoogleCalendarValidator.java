package faang.school.projectservice.validator.google;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GoogleCalendarValidator {
    private final UserServiceClient userServiceClient;

    public boolean checkUserAndEvent(long userId, long eventId) {
        UserDto userDto = userServiceClient.getUser(userId);
        return checkUserNotNull(userId) && checkEventNotNull(eventId)
                && checkUserIsParticipatorOfEvent(userDto, eventId);
    }

    private boolean checkUserNotNull(long userId) {
        if (userServiceClient.getUser(userId) == null) {
            throw new EntityNotFoundException(String.format("user with id = %s not found", userId));
        }
        return true;
    }

    private boolean checkEventNotNull(long eventId) {
        if (userServiceClient.getEventById(eventId) == null) {
            throw new EntityNotFoundException(String.format("event with id = %s not found", eventId));
        }
        return true;
    }

    private boolean checkUserIsParticipatorOfEvent(UserDto userDto, long eventId) {
        if (!userDto.getParticipatedEventIds().contains(eventId)) {
            throw new IllegalArgumentException(
                    String.format("user with id = %d is not participate of event with id = %d", userDto.getId(), eventId));
        }
        return true;
    }
}