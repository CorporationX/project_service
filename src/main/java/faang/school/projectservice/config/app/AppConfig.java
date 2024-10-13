package faang.school.projectservice.config.app;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {
    @Value("${app.name}")
    private String applicationName;

    @Value("${app.timezone}")
    private String timeZone;

    @Value("${calendar.id}")
    private String calendarId;

    @Value("${calendar.type-of-send-updates}")
    private String typeOfSendUpdates;

    @Value("${calendar.oauth.local.server.port}")
    private int calendarOauthLocalServerPort;
}
