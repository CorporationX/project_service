package faang.school.projectservice.oauth.google_oauth;

import com.google.api.client.auth.oauth2.StoredCredential;
import faang.school.projectservice.model.GoogleAuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatabaseCredentialDataStoreTest {

    @InjectMocks
    private DatabaseCredentialDataStore databaseCredentialDataStore;

    @Mock
    private TokenService tokenService;

    private GoogleAuthToken mockToken;
    private StoredCredential mockCredential;

    @BeforeEach
    public void setUp() {
        mockToken = new GoogleAuthToken();
        mockToken.setAccessToken("test-access-token");
        mockToken.setRefreshToken("test-refresh-token");
        mockToken.setExpiresIn(3600L);
        mockToken.setCreatedAt(LocalDateTime.now());

        mockCredential = new StoredCredential();
        mockCredential.setAccessToken("test-access-token");
        mockCredential.setRefreshToken("test-refresh-token");
        mockCredential.setExpirationTimeMilliseconds(
                mockToken.getCreatedAt().plusSeconds(mockToken.getExpiresIn())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
        );
    }

    @Test
    public void testGet() {
        when(tokenService.getLatestToken()).thenReturn(mockToken);

        StoredCredential credential = databaseCredentialDataStore.get("user");

        assertNotNull(credential);
        assertEquals(mockToken.getAccessToken(), credential.getAccessToken());
        assertEquals(mockToken.getRefreshToken(), credential.getRefreshToken());
        assertEquals(
                mockToken.getCreatedAt().plusSeconds(mockToken.getExpiresIn())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                credential.getExpirationTimeMilliseconds()
        );

        verify(tokenService).getLatestToken();
    }

    @Test
    public void testSet() {
        databaseCredentialDataStore.set("user", mockCredential);

        ArgumentCaptor<GoogleAuthToken> tokenCaptor = ArgumentCaptor.forClass(GoogleAuthToken.class);
        verify(tokenService).saveToken(tokenCaptor.capture());

        GoogleAuthToken savedToken = tokenCaptor.getValue();
        assertNotNull(savedToken);
        assertEquals(mockCredential.getAccessToken(), savedToken.getAccessToken());
        assertEquals(mockCredential.getRefreshToken(), savedToken.getRefreshToken());
        assertEquals(
                (float) (mockCredential.getExpirationTimeMilliseconds() - System.currentTimeMillis()) / 1000,
                savedToken.getExpiresIn(),
                1
        );
        assertNotNull(savedToken.getCreatedAt());
    }

    @Test
    public void testDelete() {
        databaseCredentialDataStore.delete("user");
        verify(tokenService).deleteToken();
    }

    @Test
    public void testClear() {
        databaseCredentialDataStore.clear();
        verify(tokenService).deleteAllTokens();
    }

    @Test
    public void testIsEmpty() {
        when(tokenService.getLatestToken()).thenReturn(null);
        assertTrue(databaseCredentialDataStore.isEmpty());

        when(tokenService.getLatestToken()).thenReturn(mockToken);
        assertFalse(databaseCredentialDataStore.isEmpty());
    }

    @Test
    public void testContainsKey() {
        when(tokenService.getLatestToken()).thenReturn(mockToken);
        assertTrue(databaseCredentialDataStore.containsKey("user"));

        when(tokenService.getLatestToken()).thenReturn(null);
        assertFalse(databaseCredentialDataStore.containsKey("user"));
    }
}