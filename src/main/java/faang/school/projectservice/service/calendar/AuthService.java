package faang.school.projectservice.service.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthService {
    private static final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/client-secret-calendar.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    public Credential getCredentials(String userId) throws IOException, GeneralSecurityException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CREDENTIALS_FILE_PATH));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        String accessToken = tokens.get("acc" + userId);
        String refreshToken = tokens.get("ref" + userId);

        if (accessToken != null && refreshToken != null) {
            return new Credential.Builder(flow.getMethod())
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientAuthentication(flow.getClientAuthentication())
                    .setTokenServerEncodedUrl(flow.getTokenServerEncodedUrl())
                    .build()
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken);
        }

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8088)
                .setCallbackPath("/callback")
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(userId);

        accessToken = credential.getAccessToken();
        refreshToken = credential.getRefreshToken();

        tokens.put(String.format("acc%s", userId), accessToken);
        tokens.put(String.format("ref%s", userId), refreshToken);

        return credential;
    }
}


