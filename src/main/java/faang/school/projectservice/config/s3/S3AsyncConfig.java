package faang.school.projectservice.config.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class S3AsyncConfig {

    private final S3Properties s3Properties;

    @Bean(name = "s3AsyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(s3Properties.getAsync().getCorePoolSize());
        executor.setMaxPoolSize(s3Properties.getAsync().getMaxPoolSize());
        executor.setQueueCapacity(s3Properties.getAsync().getQueueCapacity());
        executor.setThreadNamePrefix(s3Properties.getAsync().getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}