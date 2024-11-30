package faang.school.projectservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ThreadPoolConfig {

    @Bean(destroyMethod = "shutdown")
    ExecutorService cachedThreadPool() {
        log.info("New cached thread pool created");
        return Executors.newCachedThreadPool();
    }
}
