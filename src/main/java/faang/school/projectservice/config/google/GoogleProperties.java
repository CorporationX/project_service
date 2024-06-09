package faang.school.projectservice.config.google;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class GoogleProperties {
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Value("${google-service.application-name}")
    private String applicationName;

    @Value("${google-service.credentialsFilePath}")
    private String credentialsFilePath;

    @Value("${google-service.calendarId}")
    private String calendarId;

    @Value("${google-service.redirectUrl}")
    private String redirectUrl;

    @Value("${google-service.accessType}")
    private String accessType;

    @Value("${google-service.scopes}")
    private List<String> scopes;
}