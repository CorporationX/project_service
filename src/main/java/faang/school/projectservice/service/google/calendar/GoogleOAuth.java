package faang.school.projectservice.service.google.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleOAuth {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = new NetHttpTransport();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize HTTP transport", e);
        }
    }

    @Value("${spring.google.api.application-name}")
    private String applicationName;

    @Value("${spring.google.api.calendar.credentials.path}")
    private Resource credentialsResource;

    @Value("${spring.google.api.calendar.tokens.directory}")
    private String tokensDirectoryPath;

    @PostConstruct
    public Calendar init() throws IOException {
        Credential credential = authorize();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build();
    }

    private Credential authorize() throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(credentialsResource.getInputStream()));

        List<String> scopes = Collections.singletonList(CalendarScopes.CALENDAR);

        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(tokensDirectoryPath));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(fileDataStoreFactory)
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow,
                new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }
}