package faang.school.projectservice.config.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class FileUploadAsyncConfig {

    @Value("${task-executor.file-upload.core-pool-size}")
    private int corePoolSize;

    @Value("${task-executor.file-upload.max-pool-size}")
    private int maxPoolSize;

    @Value("${task-executor.file-upload.queue-capacity}")
    private int queueCapacity;

    @Bean(name = "fileUploadTaskExecutor")
    public ThreadPoolTaskExecutor fileUploadTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("FileUploadAsync-");
        executor.initialize();
        return executor;
    }
}
