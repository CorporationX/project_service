package faang.school.projectservice.config.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class FileUploadAsyncConfig {

    private final FileUploadExecutorProperties fileUploadExecutorProperties;

    @Bean(name = "fileUploadTaskExecutor")
    public ThreadPoolTaskExecutor fileUploadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(fileUploadExecutorProperties.getCorePoolSize());
        executor.setMaxPoolSize(fileUploadExecutorProperties.getMaxPoolSize());
        executor.setQueueCapacity(fileUploadExecutorProperties.getQueueCapacity());
        executor.setThreadNamePrefix("FileUploadAsync-");
        executor.initialize();
        return executor;
    }
}
