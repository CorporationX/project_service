package faang.school.projectservice.utils.google;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import faang.school.projectservice.model.google.GoogleToken;
import faang.school.projectservice.repository.GoogleTokenRepository;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class JpaDataStore extends AbstractDataStore<StoredCredential> {
    private final GoogleTokenRepository repository;

    public JpaDataStore(DataStoreFactory dataStoreFactory, String id, GoogleTokenRepository googleTokenRepository) {
        super(dataStoreFactory, id);
        this.repository = googleTokenRepository;
    }

    @Override
    public Set<String> keySet() throws IOException {
        List<String> oauthClientIds = repository.findAll().stream()
                .map(GoogleToken::getOauthClientId)
                .toList();
        return new HashSet<>(oauthClientIds);
    }

    @Override
    public Collection<StoredCredential> values() throws IOException {
        return repository.findAll().stream()
                .map(GoogleToken::toStoredCredential)
                .toList();
    }

    @Override
    public StoredCredential get(String key) throws IOException {
        return repository.findByOauthClientId(key)
                .map(GoogleToken::toStoredCredential)
                .orElse(null);
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential value) throws IOException {
        GoogleToken token = repository.findByOauthClientId(key)
                .orElse(GoogleToken.builder()
                        .oauthClientId(key)
                        .userId(Long.parseLong(key))
                        .build());
        token.setUuid(UUID.randomUUID().toString());
        token.setAccessToken(value.getAccessToken());
        token.setRefreshToken(value.getRefreshToken());
        token.setExpirationTimeMills(value.getExpirationTimeMilliseconds());
        token.setUpdatedAt(LocalDateTime.now());
        repository.save(token);
        return this;
    }

    @Override
    public DataStore<StoredCredential> clear() throws IOException {
        repository.deleteAll();
        return this;
    }

    @Override
    public DataStore<StoredCredential> delete(String key) throws IOException {
        repository.delete(repository.findByOauthClientId(key)
                .orElseThrow(() -> new EntityNotFoundException(String.format("token with oauthClientId = %s not found", key))));
        return this;
    }
}