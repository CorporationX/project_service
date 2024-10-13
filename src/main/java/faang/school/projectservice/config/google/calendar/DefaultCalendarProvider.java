package faang.school.projectservice.config.google.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.google.datastore.DatabaseDataStoreFactory;
import faang.school.projectservice.model.entity.GoogleToken;
import faang.school.projectservice.service.GoogleCredentialService;
import faang.school.projectservice.service.GoogleTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class DefaultCalendarProvider implements CalendarProvider {
    private final AppConfig appConfig;
    private final DatabaseDataStoreFactory databaseDataStoreFactory;
    private final GoogleTokenService googleTokenService;
    private final UserContext userContext;
    private final GoogleCredentialService googleCredentialService;

    @Override
    public Calendar getCalendar() throws GeneralSecurityException, IOException {
        Long userId = userContext.getUserId();
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        String applicationName = appConfig.getApplicationName();
        return new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT, userId))
                .setApplicationName(applicationName)
                .build();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, Long userId) throws IOException {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), clientSecrets, getScopesForUser(userId))
                .setDataStoreFactory(databaseDataStoreFactory)
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential(userId.toString());

        if (credential == null) {
            return initiateGoogleAuthorization(HTTP_TRANSPORT, userId);
        }

        if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 0) {
            boolean success = credential.refreshToken();
            if (!success) {
                return initiateGoogleAuthorization(HTTP_TRANSPORT, userId);
            }
        }

        return credential;
    }

    private Credential initiateGoogleAuthorization(final NetHttpTransport HTTP_TRANSPORT, Long userId) throws IOException {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), clientSecrets, getScopesForUser(userId))
                .setDataStoreFactory(databaseDataStoreFactory)
                .setAccessType("offline")
                .build();

        int port = appConfig.getCalendarOauthLocalServerPort();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(port).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userId.toString());
    }

    private GoogleClientSecrets loadClientSecrets() throws IOException {
        String credentialsJson = googleCredentialService.getCredentialsJson();

        InputStream targetStream = new ByteArrayInputStream(credentialsJson.getBytes());
        return GoogleClientSecrets.load(GsonFactory.getDefaultInstance(), new InputStreamReader(targetStream));
    }

    private List<String> getScopesForUser(Long userId) {
        Optional<GoogleToken> googleToken = googleTokenService.getTokenByUserId(userId);
        if (googleToken.isPresent() && googleToken.get().getScope() != null && !googleToken.get().getScope().isEmpty()) {
            return Arrays.asList(googleToken.get().getScope().split(" "));
        }
        return Collections.singletonList(CalendarScopes.CALENDAR);
    }
}
