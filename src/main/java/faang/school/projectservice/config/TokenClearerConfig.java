package faang.school.projectservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class TokenClearerConfig {

    @Bean(name = "tokenClearer")
    public Executor createClearTokenThread() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setThreadNamePrefix("TokenCleanup-");
        return executor;
    }
}
