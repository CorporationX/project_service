package faang.school.projectservice.config.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleCalendarConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.service.account.key}")
    private String serviceAccountKeyPath;

    @Value("${google.application.name}")
    private String applicationName;

    @Bean
    public Calendar googleCalendarClient() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = ServiceAccountCredentials
                .fromStream(new FileInputStream(serviceAccountKeyPath))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/calendar"));

        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY,
                new com.google.auth.http.HttpCredentialsAdapter(credentials))
                .setApplicationName(applicationName)
                .build();
    }
}
