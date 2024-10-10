package faang.school.projectservice.config.google.datastore;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import faang.school.projectservice.model.entity.GoogleToken;
import faang.school.projectservice.service.GoogleTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class DatabaseDataStore implements DataStore<StoredCredential> {

    private final GoogleTokenService googleTokenService;

    @Override
    public DataStoreFactory getDataStoreFactory() {
        return new DatabaseDataStoreFactory(googleTokenService);
    }

    @Override
    public String getId() {
        return "DatabaseDataStore";
    }

    @Override
    public int size() {
        return (int) googleTokenService.countTokens();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(String key) {
        return googleTokenService.getTokenByUserId(Long.parseLong(key)).isPresent();
    }

    @Override
    public boolean containsValue(StoredCredential value) {
        return googleTokenService.findByToken(value.getAccessToken()).isPresent();
    }

    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection<StoredCredential> values() {
        return Collections.emptyList();
    }

    @Override
    public StoredCredential get(String key) {
        Optional<GoogleToken> token = googleTokenService.getTokenByUserId(Long.parseLong(key));
        if (token.isPresent()) {
            GoogleToken googleToken = token.get();
            StoredCredential credential = new StoredCredential();
            credential.setAccessToken(googleToken.getAccessToken());
            credential.setRefreshToken(googleToken.getRefreshToken());
            credential.setExpirationTimeMilliseconds(googleToken.getExpiresIn().getTime());
            return credential;
        }
        return null;
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential value) {
        Optional<GoogleToken> existingToken = googleTokenService.getTokenByUserId(Long.parseLong(key));
        if (existingToken.isPresent()) {
            GoogleToken tokenToUpdate = existingToken.get();
            tokenToUpdate.setAccessToken(value.getAccessToken());
            tokenToUpdate.setRefreshToken(value.getRefreshToken());
            tokenToUpdate.setExpiresIn(new java.sql.Timestamp(value.getExpirationTimeMilliseconds()));
            googleTokenService.saveOrUpdateToken(tokenToUpdate);
        } else {
            Long userId = Long.parseLong(key);
            GoogleToken googleToken = new GoogleToken();
            googleToken.setUserId(userId);
            googleToken.setAccessToken(value.getAccessToken());
            googleToken.setRefreshToken(value.getRefreshToken());
            googleToken.setExpiresIn(new java.sql.Timestamp(value.getExpirationTimeMilliseconds()));
            googleTokenService.saveOrUpdateToken(googleToken);
        }
        return this;
    }

    @Override
    public DataStore<StoredCredential> delete(String key) {
        googleTokenService.deleteTokenByUserId(Long.parseLong(key));
        return this;
    }

    @Override
    public DataStore<StoredCredential> clear() {
        googleTokenService.deleteAllTokens();
        return this;
    }
}
