package faang.school.projectservice.service.google.token;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class DatabaseDataStoreFactory implements DataStoreFactory {

    private final GoogleTokenService googleTokenService;

    @Override
    public DataStore<StoredCredential> getDataStore(String id) throws IOException {
        return new DatabaseDataStore(googleTokenService);
    }
}
