package faang.school.projectservice.google;

import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface AuthorizationService {
    Calendar authorizeAndGetCalendar() throws GeneralSecurityException, IOException;
}
