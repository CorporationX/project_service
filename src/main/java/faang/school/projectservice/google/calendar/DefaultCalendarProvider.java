package faang.school.projectservice.google.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DefaultCalendarProvider implements CalendarProvider {
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private final AppConfig appConfig;

    @Override
    public Calendar getCalendar() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        String applicationName = appConfig.getApplicationName();
        return new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT))
                .setApplicationName(applicationName)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = DefaultCalendarProvider.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException(String.format("Resource not found: %s", CREDENTIALS_FILE_PATH));
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        int port = appConfig.getCalendarOauthLocalServerPort();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(port).build();
        //TODO переписать код: в authorize передавать userId, токены сохранять в таблице БД
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
