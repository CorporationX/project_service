package faang.school.projectservice.oauth.google_oauth;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import faang.school.projectservice.exceptions.google_calendar.exceptions.NotFoundException;
import faang.school.projectservice.model.GoogleAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseCredentialDataStore implements DataStore<StoredCredential> {
    private final TokenService tokenService;
    private static final String USER_KEY = "user";
    private static final String STORED_CREDENTIAL_ID = "StoredCredential";

    @Override
    public DataStoreFactory getDataStoreFactory() {
        return new DataStoreFactory() {
            @Override
            public <V extends Serializable> DataStore<V> getDataStore(String id) throws IOException {
                if (STORED_CREDENTIAL_ID.equals(id)) {
                    return (DataStore<V>) DatabaseCredentialDataStore.this;
                }
                throw new IOException("Unsupported DataStore ID: " + id);
            }
        };
    }

    @Override
    public String getId() {
        return STORED_CREDENTIAL_ID;
    }

    @Override
    public int size() {
        return tokenService.getLatestToken() != null ? 1 : 0;
    }

    @Override
    public boolean isEmpty() {
        return tokenService.getLatestToken() == null;
    }

    @Override
    public boolean containsKey(String key) {
        return USER_KEY.equals(key) && tokenService.getLatestToken() != null;
    }

    @Override
    public boolean containsValue(StoredCredential value) {
        return value.equals(get(USER_KEY));
    }

    @Override
    public Set<String> keySet() {
        return Collections.singleton(USER_KEY);
    }

    @Override
    public Collection<StoredCredential> values() {
        StoredCredential credential = get(USER_KEY);
        return credential != null ? Collections.singletonList(credential) : Collections.emptyList();
    }

    @Override
    public StoredCredential get(String key) {
        if (!USER_KEY.equals(key)) {
            throw new NotFoundException("Unsupported key: " + key);
        }

        GoogleAuthToken token = tokenService.getLatestToken();
        if (token == null) {
            throw new NotFoundException("Token not found");
        }

        StoredCredential credential = new StoredCredential();
        credential.setAccessToken(token.getAccessToken());
        credential.setRefreshToken(token.getRefreshToken());
        long expirationTime = token.getCreatedAt()
                .plusSeconds(token.getExpiresIn())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        credential.setExpirationTimeMilliseconds(expirationTime);

        return credential;
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential value) {
        if (!USER_KEY.equals(key)) {
            return this;
        }

        GoogleAuthToken token = tokenService.getLatestToken();
        if (token == null) {
            token = new GoogleAuthToken();
        }

        token.setAccessToken(value.getAccessToken());
        token.setRefreshToken(value.getRefreshToken());
        token.setExpiresIn(value.getExpirationTimeMilliseconds() != null ?
                (value.getExpirationTimeMilliseconds() - System.currentTimeMillis()) / 1000 : null);
        token.setCreatedAt(LocalDateTime.now());

        tokenService.saveToken(token);
        return this;
    }

    @Override
    public DataStore<StoredCredential> delete(String key) {
        if (USER_KEY.equals(key)) {
            tokenService.deleteToken();
        }
        return this;
    }

    @Override
    public DataStore<StoredCredential> clear() {
        tokenService.deleteAllTokens();
        return this;
    }
}