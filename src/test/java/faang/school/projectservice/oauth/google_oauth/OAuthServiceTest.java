package faang.school.projectservice.oauth.google_oauth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import faang.school.projectservice.exceptions.google_calendar.exceptions.BadRequestException;
import faang.school.projectservice.exceptions.google_calendar.exceptions.GoogleCalendarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OAuthServiceTest {
    private static final String REDIRECT_URI = "http://localhost/callback";
    private static final String TEST_CODE = "test-code";
    private static final String SUCCESS_MESSAGE = "Авторизация успешна. Токены сохранены.";
    private static final String ERROR_MESSAGE = "Ошибка авторизации: ";
    private static final String CALLBACK_ERROR = "Ошибка обработки OAuth callback";

    @InjectMocks
    private OAuthService oauthService;

    @Mock
    private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlowMock;

    @Mock
    private GoogleAuthorizationCodeTokenRequest tokenRequestMock;

    @Mock
    private GoogleTokenResponse tokenResponseMock;

    @Mock
    private Credential credentialMock;

    @BeforeEach
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(oauthService, "redirectUri", REDIRECT_URI);
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

        String result = oauthService.handleOAuthCallback(TEST_CODE);

        assertEquals(SUCCESS_MESSAGE, result);
        verify(googleAuthorizationCodeFlowMock).createAndStoreCredential(tokenResponseMock, "user");
    }

    @Test
    public void testHandleOAuthCallback_IOException() throws IOException {
        when(googleAuthorizationCodeFlowMock.newTokenRequest(TEST_CODE)).thenReturn(tokenRequestMock);
        when(tokenRequestMock.setRedirectUri(REDIRECT_URI)).thenReturn(tokenRequestMock);
        when(tokenRequestMock.execute()).thenThrow(new IOException("Test IOException"));

        Exception exception = assertThrows(GoogleCalendarException.class, () ->
                oauthService.handleOAuthCallback(TEST_CODE));

        assertEquals(CALLBACK_ERROR, exception.getMessage());
        verify(googleAuthorizationCodeFlowMock, never()).createAndStoreCredential(any(), any());
    }
}