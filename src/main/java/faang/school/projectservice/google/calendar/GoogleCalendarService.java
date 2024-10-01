package faang.school.projectservice.google.calendar;

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
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.dto.google.calendar.CalendarEventDto;
import faang.school.projectservice.dto.google.calendar.CalendarEventStatus;
import faang.school.projectservice.mapper.calendar.CalendarEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GoogleCalendarService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final CalendarEventMapper calendarEventMapper;
    private final AppConfig appConfig;

    public CalendarEventDto createEvent(CalendarEventDto calendarEventDto) throws GeneralSecurityException, IOException {
        Calendar calendar = getCalendar();
        Event event = calendarEventMapper.toEvent(calendarEventDto);
        String calendarId = appConfig.getCalendarId();
        String typeOfSendUpdate = appConfig.getTypeOfSendUpdates();
        event = calendar.events().insert(calendarId, event)
                .setSendUpdates(typeOfSendUpdate)
                .execute();
        return calendarEventMapper.toDto(event);
    }

    public CalendarEventDto update(CalendarEventDto dto) throws GeneralSecurityException, IOException {
        Calendar calendar = getCalendar();
        String calendarId = appConfig.getCalendarId();
        Event event = calendar.events().get(calendarId, dto.getId()).execute();

        event.setSummary(dto.getSummary());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());

        EventDateTime start = convertToEventDateTime(dto.getStartDate(), dto.getTimeZone());
        event.setStart(start);

        EventDateTime end = convertToEventDateTime(dto.getEndDate(), dto.getTimeZone());
        event.setEnd(end);

        if (!event.getStatus().equals(CalendarEventStatus.CANCELLED.toString())) {
            event.setAttendees(calendarEventMapper.toEventAttendees(dto.getAttendeeEmails()));
        }

        String typeOfSendUpdate = appConfig.getTypeOfSendUpdates();
        Event updatedEvent = calendar.events().update(calendarId, dto.getId(), event)
                .setSendUpdates(typeOfSendUpdate)
                .execute();

        return calendarEventMapper.toDto(updatedEvent);
    }

    public void delete(String calendarEventId) throws GeneralSecurityException, IOException {
        String calendarId = appConfig.getCalendarId();
        Calendar calendar = getCalendar();
        String typeOfSendUpdate = appConfig.getTypeOfSendUpdates();
        calendar.events().delete(calendarId, calendarEventId)
                .setSendUpdates(typeOfSendUpdate)
                .execute();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Calendar getCalendar() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        String applicationName = appConfig.getApplicationName();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(applicationName)
                .build();
    }

    private EventDateTime convertToEventDateTime(LocalDateTime localDateTime, String timeZone) {
        if (localDateTime == null || timeZone == null) {
            return null;
        }
        return new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(java.util.Date.from(localDateTime.atZone(ZoneId.of(timeZone)).toInstant())))
                .setTimeZone(timeZone);
    }
}
