package faang.school.projectservice.service.project.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import faang.school.projectservice.model.CalendarToken;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CalendarTokenRepository;
import faang.school.projectservice.testData.project.OAuthServiceTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleAuthorizationServiceTest {
    public static final int ACCESS_TOKEN_EXPIRES_IN_SECONDS = 100;
    @Spy
    @InjectMocks
    private GoogleAuthorizationService oAuthService;
    @Mock
    private CalendarTokenRepository calendarTokenRepository;
    private OAuthServiceTestData testData;
    private GoogleAuthorizationCodeFlow flow;

    private static void changeField(Object obj, String fieldName, Object newValue) {
        try {
            // Получаем поле
            Field field = obj.getClass().getDeclaredField(fieldName);

            // Установка доступа к приватному полю
            field.setAccessible(true);

            // Изменение значения поля
            field.set(obj, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        testData = new OAuthServiceTestData();
        flow = mock(GoogleAuthorizationCodeFlow.class);

        changeField(oAuthService, "clientId", testData.getClientId());
        changeField(oAuthService, "clientSecret", testData.getClientSecret());
        changeField(oAuthService, "accessType", testData.getAccessType());
        changeField(oAuthService, "flow", flow);
        changeField(oAuthService, "redirectUri", "url");
        changeField(oAuthService, "accessTokenExpiresInSeconds", ACCESS_TOKEN_EXPIRES_IN_SECONDS);
        oAuthService.setUp();
    }

    @Nested
    class PositiveTests {
        @Test
        void generateCredentialsTest() {
            var credentialsCaptor = ArgumentCaptor.forClass(Credential.class);
            var calendarTokenCaptor = ArgumentCaptor.forClass(CalendarToken.class);
            CalendarToken calendarToken = mock(CalendarToken.class);
            when(calendarToken.getUpdatedAt())
                    .thenReturn(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));

            oAuthService.generateCredential(calendarToken);

            verify(oAuthService).refreshToken(calendarTokenCaptor.capture(), credentialsCaptor.capture());
            assertEquals(calendarToken, calendarTokenCaptor.getValue());
        }

        @Test
        void refreshTokenTest() throws IOException {
            Credential credential = mock(Credential.class);
            var calendarToken = testData.getCalendarToken();
            var calendarTokenCaptor = ArgumentCaptor.forClass(CalendarToken.class);

            when(credential.getExpiresInSeconds()).thenReturn(10L);
            when(credential.getAccessToken()).thenReturn(testData.getAccessToken());
            when(credential.getRefreshToken()).thenReturn(testData.getRefreshToken());

            assertDoesNotThrow(() -> oAuthService.refreshToken(calendarToken, credential));

            verify(credential).refreshToken();
            verify(calendarTokenRepository).save(calendarTokenCaptor.capture());
            assertEquals(calendarToken, calendarTokenCaptor.getValue());
        }

        @Test
        void authorizeProjectTest() {
            when(calendarTokenRepository.findByProjectId(anyLong()))
                    .thenReturn(Optional.of(testData.getCalendarToken()));

            assertDoesNotThrow(() -> oAuthService.authorizeProject(Project.builder().id(12L).build(), "code"));

            verify(calendarTokenRepository, times(0)).save(any(CalendarToken.class));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        void refreshTokenTest() throws IOException {
            Credential credential = mock(Credential.class);
            var calendarToken = testData.getCalendarToken();
            when(credential.getExpiresInSeconds()).thenReturn(testData.getExpiresInSeconds() + ACCESS_TOKEN_EXPIRES_IN_SECONDS);

            assertDoesNotThrow(() -> oAuthService.refreshToken(calendarToken, credential));

            verifyNoInteractions(calendarTokenRepository);
            verify(credential, times(0)).refreshToken();
        }

        @Test
        void authorizeProjectTest() {
            var tokenResponse = mock(TokenResponse.class);

            when(calendarTokenRepository.findByProjectId(anyLong())).thenReturn(Optional.empty());
            doReturn(tokenResponse).when(oAuthService).requestToken(anyString());
            when(tokenResponse.getAccessToken()).thenReturn(testData.getAccessToken());
            when(tokenResponse.getRefreshToken()).thenReturn(testData.getRefreshToken());

            assertDoesNotThrow(() -> oAuthService.authorizeProject(Project.builder().id(12L).build(), "code"));

            verify(calendarTokenRepository).save(any(CalendarToken.class));
        }

    }
}