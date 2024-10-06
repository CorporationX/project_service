package faang.school.projectservice.config.google_calendar_config;

import com.google.api.client.util.store.DataStoreFactory;
import faang.school.projectservice.oauth.google_oauth.DatabaseCredentialDataStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@RequiredArgsConstructor
public class DataStoreConfig {
    private final DatabaseCredentialDataStore databaseCredentialDataStore;
    @Bean
    public DataStoreFactory dataStoreFactory() {
        return databaseCredentialDataStore.getDataStoreFactory();
    }
}