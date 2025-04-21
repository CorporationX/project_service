package faang.school.projectservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class TokenClearerConfig {

    @Value("${clearing-configs-properties.poolSize}")
    private int poolSize;

    @Bean(name = "tokenClearer")
    public Executor createClearTokenThread() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setThreadNamePrefix("TokenCleanup-");
        return executor;
    }
}
