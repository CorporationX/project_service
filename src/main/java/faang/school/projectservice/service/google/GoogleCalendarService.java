package faang.school.projectservice.service.google;

import java.io.IOException;

public interface GoogleCalendarService {
    String authorizeUser(String authorizationCode, Long userId) throws IOException;
    String createEvent(Long userId, Long eventId) throws IOException;
}