package faang.school.projectservice.config;

//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.CalendarScopes;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.GoogleCredentials;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Collections;
//
//@Slf4j
//@Configuration
//public class CalendarConfig {
//
//    @Value("${google.calendar.application-name}")
//    private String APPLICATION_NAME;
//
//    @Value("${google.calendar.credentials-path}")
//    private String CREDENTIALS_FILE_PATH;
//
//    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//
//    @Bean
//    public Calendar googleCalendar() throws GeneralSecurityException, IOException {
//        FileInputStream serviceAccountStream = new FileInputStream(CREDENTIALS_FILE_PATH);
//        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
//                .createScoped(Collections.singleton(CalendarScopes.CALENDAR));
//
//        return new Calendar.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JSON_FACTORY,
//                new HttpCredentialsAdapter(credentials))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//}