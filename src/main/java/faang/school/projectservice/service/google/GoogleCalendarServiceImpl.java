package faang.school.projectservice.service.google;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.google.GoogleCalendarConfig;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.event.EventDto;
import faang.school.projectservice.repository.GoogleTokenRepository;
import faang.school.projectservice.validator.google.GoogleCalendarValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class GoogleCalendarServiceImpl implements GoogleCalendarService {
    private final GoogleCalendarConfig googleCalendarConfig;
    private final GoogleCalendarValidator googleCalendarValidator;
    private final UserServiceClient userServiceClient;
    private final GoogleTokenRepository googleTokenRepository;

    @Override
    public String authorizeUser(String authorizationCode, Long userId) throws IOException {
        googleCalendarConfig.authorizeUser(authorizationCode, userId);
        return String.format("user with id = %d authorized successfully", userId);
    }

    @Override
    public String createEvent(Long userId, Long eventId) throws IOException {
        googleCalendarValidator.checkUserAndEvent(userId, eventId);
        UserDto userDto = userServiceClient.getUser(userId);
        EventDto eventDto = userServiceClient.getEventById(eventId);
        if (!googleTokenRepository.existsGoogleTokenByUserId(userId)) {
            return String.format("follow the link to authorize in calendar: %s", googleCalendarConfig.getAuthorizationUrl(userId, eventId));
        }
        String calendarId = googleCalendarConfig.getGoogleProperties().getCalendarId();
        Event event = googleCalendarConfig.createEvent(eventDto);
        Calendar calendar = googleCalendarConfig.createService(userDto);
        Event result = calendar.events().insert(calendarId, event).execute();
        return String.format("follow the link to enter your event in calendar: %s", result.getHtmlLink());
    }
}