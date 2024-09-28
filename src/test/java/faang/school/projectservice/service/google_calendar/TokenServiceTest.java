package faang.school.projectservice.service.google_calendar;

import faang.school.projectservice.model.GoogleAuthToken;
import faang.school.projectservice.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    private GoogleAuthToken mockToken;

    @BeforeEach
    public void setUp() {
        mockToken = new GoogleAuthToken();
        mockToken.setAccessToken("test-access-token");
        mockToken.setRefreshToken("test-refresh-token");
        mockToken.setExpiresIn(3600L);
        mockToken.setScope("test-scope");
        mockToken.setTokenType("Bearer");
    }

    @Test
    public void testSaveToken() {
        tokenService.saveToken(mockToken);

        ArgumentCaptor<GoogleAuthToken> tokenCaptor = ArgumentCaptor.forClass(GoogleAuthToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        GoogleAuthToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken);
        assertEquals("test-access-token", savedToken.getAccessToken());
        assertEquals("test-refresh-token", savedToken.getRefreshToken());
        assertEquals(3600L, savedToken.getExpiresIn());
        assertEquals("test-scope", savedToken.getScope());
        assertEquals("Bearer", savedToken.getTokenType());
    }

    @Test
    public void testGetLatestToken() {
        when(tokenRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(mockToken);

        GoogleAuthToken latestToken = tokenService.getLatestToken();

        assertNotNull(latestToken);
        assertEquals("test-access-token", latestToken.getAccessToken());
        assertEquals("test-refresh-token", latestToken.getRefreshToken());
        assertEquals(3600L, latestToken.getExpiresIn());
        assertEquals("test-scope", latestToken.getScope());
        assertEquals("Bearer", latestToken.getTokenType());
        verify(tokenRepository).findFirstByOrderByCreatedAtDesc();
    }
}