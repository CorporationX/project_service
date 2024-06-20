package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.calendar.CalendarToken;

import java.net.URL;

public interface GoogleAuthorizationService {
    Credential generateCredential(CalendarToken calendarToken);

    void refreshToken(CalendarToken calendarToken, Credential credential);

    CalendarToken authorizeProject(Project project, String code);

    TokenResponse requestToken(String code);

    URL getAuthUrl();
}
