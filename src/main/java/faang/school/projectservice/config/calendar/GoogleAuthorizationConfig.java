package faang.school.projectservice.config.calendar;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

@Configuration
public class GoogleAuthorizationConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_EVENTS, CalendarScopes.CALENDAR);

    @Bean
    public JsonFactory jsonFactory() {
        return JSON_FACTORY;
    }

    @Bean
    public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
        return GoogleNetHttpTransport.newTrustedTransport();
    }

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow(Environment env, NetHttpTransport httpTransport) {
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY,
                Objects.requireNonNull(env.getProperty("google.clientId")),
                Objects.requireNonNull(env.getProperty("google.clientSecret")), SCOPES)
                .setAccessType(env.getProperty("google.accessType"))
                .setAccessType("offline")
                .build();
    }
}

