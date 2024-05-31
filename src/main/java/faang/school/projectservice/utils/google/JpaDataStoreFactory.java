package faang.school.projectservice.utils.google;

import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import faang.school.projectservice.repository.GoogleTokenRepository;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class JpaDataStoreFactory implements DataStoreFactory {
    private final GoogleTokenRepository googleTokenRepository;

    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String id) {
        return (DataStore<V>) new JpaDataStore(this, id, googleTokenRepository);
    }
}