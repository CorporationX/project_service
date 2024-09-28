package faang.school.projectservice.oauth.google_oauth;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import faang.school.projectservice.model.GoogleAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseCredentialDataStore implements DataStore<StoredCredential> {
    private final TokenService tokenService;
    private static final String USER_KEY = "user";
    private static final String STORED_CREDENTIAL_ID = "StoredCredential";

    @Override
    public DataStoreFactory getDataStoreFactory() {
        log.info("Получение фабрики хранилища данных");
        return new DataStoreFactory() {
            @Override
            public <V extends Serializable> DataStore<V> getDataStore(String id) throws IOException {
                log.info("Запрос хранилища данных с ID: {}", id);
                if (STORED_CREDENTIAL_ID.equals(id)) {
                    return (DataStore<V>) DatabaseCredentialDataStore.this;
                }
                log.error("Unsupported DataStore ID: " + id);
                throw new IOException("Unsupported DataStore ID: " + id);
            }
        };
    }

    @Override
    public String getId() {
        log.info("Получение ID хранилища данных");
        return STORED_CREDENTIAL_ID;
    }

    @Override
    public int size() {
        log.info("Получение размера хранилища данных");
        return tokenService.getLatestToken() != null ? 1 : 0;
    }

    @Override
    public boolean isEmpty() {
        log.info("Проверка, пусто ли хранилище данных");
        return tokenService.getLatestToken() == null;
    }

    @Override
    public boolean containsKey(String key) {
        log.info("Проверка, содержит ли хранилище данных ключ: {}", key);
        return USER_KEY.equals(key) && tokenService.getLatestToken() != null;
    }

    @Override
    public boolean containsValue(StoredCredential value) {
        log.info("Проверка, содержит ли хранилище данных значение");
        return value.equals(get(USER_KEY));
    }

    @Override
    public Set<String> keySet() {
        log.info("Получение набора ключей из хранилища данных");
        return Collections.singleton(USER_KEY);
    }

    @Override
    public Collection<StoredCredential> values() {
        log.info("Получение всех значений из хранилища данных");
        StoredCredential credential = get(USER_KEY);
        return credential != null ? Collections.singletonList(credential) : Collections.emptyList();
    }

    @Override
    public StoredCredential get(String key) {
        log.info("Получение значения из хранилища данных по ключу: {}", key);
        if (!USER_KEY.equals(key)) {
            log.warn("Неподдерживаемый ключ: {}", key);
            return null;
        }

        GoogleAuthToken token = tokenService.getLatestToken();
        if (token == null) {
            log.warn("Токен не найден в базе данных");
            return null;
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

        log.info("Токен успешно получен и преобразован в StoredCredential");
        return credential;
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential value) {
        log.info("Сохранение значения в хранилище данных с ключом: {}", key);
        if (!USER_KEY.equals(key)) {
            log.warn("Неподдерживаемый ключ: {}", key);
            return this;
        }

        GoogleAuthToken token = tokenService.getLatestToken();
        if (token == null) {
            log.info("Создание нового токена");
            token = new GoogleAuthToken();
        }

        token.setAccessToken(value.getAccessToken());
        token.setRefreshToken(value.getRefreshToken());
        token.setExpiresIn(value.getExpirationTimeMilliseconds() != null ?
                (value.getExpirationTimeMilliseconds() - System.currentTimeMillis()) / 1000 : null);
        token.setCreatedAt(LocalDateTime.now());

        tokenService.saveToken(token);
        log.info("Токен успешно сохранен в базе данных");
        return this;
    }

    @Override
    public DataStore<StoredCredential> delete(String key) {
        log.info("Удаление значения из хранилища данных с ключом: {}", key);
        if (USER_KEY.equals(key)) {
            tokenService.deleteToken();
            log.info("Токен успешно удален из базы данных");
        } else {
            log.warn("Неподдерживаемый ключ: {}", key);
        }
        return this;
    }

    @Override
    public DataStore<StoredCredential> clear() {
        log.info("Очистка хранилища данных");
        tokenService.deleteAllTokens();
        log.info("Все токены успешно удалены из базы данных");
        return this;
    }
}