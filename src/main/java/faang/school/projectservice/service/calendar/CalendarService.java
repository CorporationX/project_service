package faang.school.projectservice.service.calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CalendarService {
    void addEventToCalendar(long eventId, String calendarId) throws GeneralSecurityException, IOException;

    void update();

    void view() throws GeneralSecurityException, IOException;
}
