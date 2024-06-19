package faang.school.projectservice.testData.project;

import faang.school.projectservice.model.CalendarToken;
import lombok.Getter;

@Getter
public class OAuthServiceTestData {
    private final CalendarToken calendarToken;
    private final String clientId = "clientId";
    private final String clientSecret = "clientSecret";
    private final String accessType = "offline";
    private final Long expiresInSeconds = 3000L;
    private final String accessToken = "accessToken";
    private final String refreshToken = "refreshToken";

    public OAuthServiceTestData() {
        calendarToken = createCalendarToken();
    }

    private CalendarToken createCalendarToken() {
        return CalendarToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
