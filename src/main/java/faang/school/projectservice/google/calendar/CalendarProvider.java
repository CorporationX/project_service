package faang.school.projectservice.google.calendar;

import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface CalendarProvider {
    Calendar getCalendar() throws GeneralSecurityException, IOException;
}
