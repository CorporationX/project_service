package faang.school.projectservice.service.google_calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import faang.school.projectservice.exceptions.google_calendar.exceptions.BadRequestException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import faang.school.projectservice.model.GoogleAuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OAuthServiceTest {
    private static final String CLIENT_ID = "test-client-id";
    private static final String CLIENT_SECRET = "test-client-secret";
    private static final String REDIRECT_URI = "http://localhost/callback";
    private static final String TEST_CODE = "test-code";
    private static final String SUCCESS_MESSAGE = "Авторизация успешна. Токены сохранены.";
    private static final String ERROR_MESSAGE = "Ошибка авторизации: ";
    private static final String CALLBACK_ERROR = "Ошибка обработки OAuth callback";
    private static final String INIT_ERROR = "Ошибка инициализации Google OAuth Flow";

    @InjectMocks
    private OAuthService oauthService;

    @Mock
    private TokenService tokenService;

    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlowMock;
    private GoogleAuthorizationCodeTokenRequest tokenRequestMock;
    private GoogleTokenResponse tokenResponseMock;
    private Credential credentialMock;

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(oauthService, "clientId", CLIENT_ID);
        ReflectionTestUtils.setField(oauthService, "clientSecret", CLIENT_SECRET);
        ReflectionTestUtils.setField(oauthService, "redirectUri", REDIRECT_URI);

        Method postConstruct = OAuthService.class.getDeclaredMethod("initializeGoogleAuthorizationCodeFlow");
        postConstruct.setAccessible(true);
        postConstruct.invoke(oauthService);

        googleAuthorizationCodeFlowMock = mock(GoogleAuthorizationCodeFlow.class);
        tokenRequestMock = mock(GoogleAuthorizationCodeTokenRequest.class);
        tokenResponseMock = mock(GoogleTokenResponse.class);
        credentialMock = mock(Credential.class);

        ReflectionTestUtils.setField(oauthService, "googleAuthorizationCodeFlow", googleAuthorizationCodeFlowMock);
    }

    @Test
    public void testBuildAuthorizationUrl() {
        GoogleAuthorizationCodeRequestUrl requestUrlMock = mock(GoogleAuthorizationCodeRequestUrl.class);
        when(googleAuthorizationCodeFlowMock.newAuthorizationUrl()).thenReturn(requestUrlMock);
        when(requestUrlMock.setRedirectUri(anyString())).thenReturn(requestUrlMock);
        when(requestUrlMock.setState(anyString())).thenReturn(requestUrlMock);
        when(requestUrlMock.setAccessType(anyString())).thenReturn(requestUrlMock);
        when(requestUrlMock.setApprovalPrompt(anyString())).thenReturn(requestUrlMock);
        when(requestUrlMock.build()).thenReturn("http://authorization.url");

        String authorizationUrl = oauthService.buildAuthorizationUrl();

        assertNotNull(authorizationUrl);
        assertEquals("http://authorization.url", authorizationUrl);
        verify(googleAuthorizationCodeFlowMock).newAuthorizationUrl();
        verify(requestUrlMock).setRedirectUri(REDIRECT_URI);
        verify(requestUrlMock).setAccessType("offline");
        verify(requestUrlMock).setApprovalPrompt("force");
    }

    @Test
    public void testProcessOAuthCallback_withError() {
        String error = "access_denied";
        Exception exception = assertThrows(BadRequestException.class, () ->
            oauthService.processOAuthCallback(null, error));
        assertEquals(ERROR_MESSAGE + error, exception.getMessage());
    }

    @Test
    public void testProcessOAuthCallback_withCode() {
        OAuthService oauthServiceSpy = spy(oauthService);
        doReturn(SUCCESS_MESSAGE).when(oauthServiceSpy).handleOAuthCallback(TEST_CODE);

        String result = oauthServiceSpy.processOAuthCallback(TEST_CODE, null);

        assertEquals(SUCCESS_MESSAGE, result);
        verify(oauthServiceSpy).handleOAuthCallback(TEST_CODE);
    }

    @Test
    public void testHandleOAuthCallback_success() throws IOException {
        when(googleAuthorizationCodeFlowMock.newTokenRequest(TEST_CODE)).thenReturn(tokenRequestMock);
        when(tokenRequestMock.setRedirectUri(REDIRECT_URI)).thenReturn(tokenRequestMock);
        when(tokenRequestMock.execute()).thenReturn(tokenResponseMock);
        when(googleAuthorizationCodeFlowMock.createAndStoreCredential(tokenResponseMock, "user")).thenReturn(credentialMock);
        when(credentialMock.getAccessToken()).thenReturn("test-access-token");
        when(credentialMock.getRefreshToken()).thenReturn("test-refresh-token");
        when(credentialMock.getExpiresInSeconds()).thenReturn(3600L);
        when(tokenResponseMock.getScope()).thenReturn("test-scope");
        when(tokenResponseMock.getTokenType()).thenReturn("Bearer");

        String result = oauthService.handleOAuthCallback(TEST_CODE);

        assertEquals(SUCCESS_MESSAGE, result);
        ArgumentCaptor<GoogleAuthToken> tokenCaptor = ArgumentCaptor.forClass(GoogleAuthToken.class);
        verify(tokenService).saveToken(tokenCaptor.capture());

        GoogleAuthToken savedToken = tokenCaptor.getValue();
        assertEquals("test-access-token", savedToken.getAccessToken());
        assertEquals("test-refresh-token", savedToken.getRefreshToken());
        assertEquals(3600L, savedToken.getExpiresIn());
        assertEquals("test-scope", savedToken.getScope());
        assertEquals("Bearer", savedToken.getTokenType());
        assertNotNull(savedToken.getCreatedAt());
    }

    @Test
    public void testHandleOAuthCallback_IOException() throws IOException {
        when(googleAuthorizationCodeFlowMock.newTokenRequest(TEST_CODE)).thenReturn(tokenRequestMock);
        when(tokenRequestMock.setRedirectUri(REDIRECT_URI)).thenReturn(tokenRequestMock);
        when(tokenRequestMock.execute()).thenThrow(new IOException("Test IOException"));

        Exception exception = assertThrows(GoogleCalendarException.class, () ->
            oauthService.handleOAuthCallback(TEST_CODE));

        assertEquals(CALLBACK_ERROR, exception.getMessage());
        verify(tokenService, never()).saveToken(any());
    }

    @Test
    public void testInitializeGoogleAuthorizationCodeFlow_success() {
        assertNotNull(ReflectionTestUtils.getField(oauthService, "googleAuthorizationCodeFlow"));
    }

    @Test
    public void testInitializeGoogleAuthorizationCodeFlow_exception() {
        OAuthService oauthServiceSpy = spy(new OAuthService(tokenService));
        ReflectionTestUtils.setField(oauthServiceSpy, "clientId", CLIENT_ID);
        ReflectionTestUtils.setField(oauthServiceSpy, "clientSecret", CLIENT_SECRET);
        ReflectionTestUtils.setField(oauthServiceSpy, "redirectUri", REDIRECT_URI);

        try (MockedStatic<GoogleNetHttpTransport> mockedTransport = mockStatic(GoogleNetHttpTransport.class)) {
            mockedTransport.when(GoogleNetHttpTransport::newTrustedTransport)
                    .thenThrow(new GeneralSecurityException("Test Exception"));

            Exception exception = assertThrows(InvocationTargetException.class, () -> {
                Method postConstruct = OAuthService.class.getDeclaredMethod("initializeGoogleAuthorizationCodeFlow");
                postConstruct.setAccessible(true);
                postConstruct.invoke(oauthServiceSpy);
            });

            Throwable cause = exception.getCause();
            assertTrue(cause instanceof GoogleCalendarException);
            assertEquals(INIT_ERROR, cause.getMessage());
        }
    }
}