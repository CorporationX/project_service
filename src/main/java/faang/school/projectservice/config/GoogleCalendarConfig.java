package faang.school.projectservice.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
public class GoogleCalendarConfig {

    private final List<String> scopes = Collections.singletonList(CalendarScopes.CALENDAR);
    private final String applicationName = "Google Calendar";
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Value("${google.calendar.credentials.filePath}")
    private String credentialsFilePath;

    @Value("${google.calendar.credentials.tokensPath}")
    private String tokens;

    @Bean
    public Calendar googleCalendar() {
        NetHttpTransport transport;
        Credential credentials;
        try {
            transport = GoogleNetHttpTransport.newTrustedTransport();
            credentials = getCredentials(transport);
            return new Calendar.Builder(transport, jsonFactory, credentials)
                    .setApplicationName(applicationName)
                    .build();
        } catch (Exception e) {
            log.error("Ошибка конфигурирования календаря {}", e.getMessage());
            throw new RuntimeException("Ошибка конфигурирования календаря {}");
        }
    }

    private Credential getCredentials(NetHttpTransport transport) throws IOException {
        InputStream in = GoogleCalendarConfig.class.getResourceAsStream(credentialsFilePath);
        if (in == null) {
            log.error("Google Credentials Resource not found: {}", credentialsFilePath);
            return null;
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File(tokens)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8082)
                .setCallbackPath("/login/oauth2/code/google")
                .build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
