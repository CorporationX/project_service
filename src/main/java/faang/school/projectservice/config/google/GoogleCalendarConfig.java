package faang.school.projectservice.config.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.event.EventDto;
import faang.school.projectservice.repository.GoogleTokenRepository;
import faang.school.projectservice.utils.google.JpaDataStoreFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@AllArgsConstructor
@Getter
@Configuration
public class GoogleCalendarConfig {
    private static final NetHttpTransport HTTP_TRANSPORT;
    private final GoogleProperties googleProperties;
    private final GoogleTokenRepository googleTokenRepository;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            log.error("ошибка при создании авторизованной клиентской службы: ", e);
            throw new RuntimeException();
        }
    }

    public String getAuthorizationUrl(Long userId, Long eventId) throws IOException {
        GoogleClientSecrets googleClientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow codeFlow = getCodeFlow(googleClientSecrets);
        return codeFlow.newAuthorizationUrl()
                .setRedirectUri(googleProperties.getRedirectUrl())
                .setState(userId + "-" + eventId)
                .build();
    }

    public void authorizeUser(String authorizationCode, Long userId) throws IOException {
        GoogleClientSecrets googleClientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow codeFlow = getCodeFlow(googleClientSecrets);
        GoogleTokenResponse tokenResponse = codeFlow.newTokenRequest(authorizationCode)
                .setRedirectUri(googleProperties.getRedirectUrl())
                .execute();
        codeFlow.createAndStoreCredential(tokenResponse, String.valueOf(userId));
    }

    public Calendar createService(UserDto userDto) throws IOException {
        GoogleClientSecrets clientSecrets = getClientSecrets();
        GoogleAuthorizationCodeFlow codeFlow = getCodeFlow(clientSecrets);
        Credential credential = codeFlow.loadCredential(String.valueOf(userDto.getId()));
        return getService(credential);
    }


    public Event createEvent(EventDto eventDto) {
        Event event = new Event();
        event.setSummary(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        EventDateTime startDateTime = convertLocalDateTimeToEventDateTime(eventDto.getStartDate());
        EventDateTime endDateTime = convertLocalDateTimeToEventDateTime(eventDto.getEndDate());
        event.setStart(startDateTime);
        event.setEnd(endDateTime);
        return event;
    }

    private GoogleClientSecrets getClientSecrets() throws IOException {
        String filePath = googleProperties.getCredentialsFilePath();
        InputStream inputStream = GoogleCalendarConfig.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            log.error("файл для получения информации по пути {} не найден", filePath);
            throw new FileNotFoundException("file not found: " + filePath);
        }
        return GoogleClientSecrets.load(googleProperties.getJsonFactory(), new InputStreamReader(inputStream));
    }

    private GoogleAuthorizationCodeFlow getCodeFlow(GoogleClientSecrets clientSecrets) throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, googleProperties.getJsonFactory(), clientSecrets,
                googleProperties.getScopes())
                .setDataStoreFactory(new JpaDataStoreFactory(googleTokenRepository))
                .setAccessType(googleProperties.getAccessType())
                .build();
    }

    private EventDateTime convertLocalDateTimeToEventDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTime dateTime = new DateTime(zonedDateTime.toInstant().toEpochMilli());
        return new EventDateTime()
                .setDateTime(dateTime);
    }

    private Calendar getService(Credential credential) {
        return new Calendar.Builder(HTTP_TRANSPORT, credential.getJsonFactory(), credential)
                .build();
    }
}