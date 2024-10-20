package faang.school.projectservice.config.meet;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "meetCompletionThreadPool")
    public ExecutorService meetCompletionThreadPool() {
        return Executors.newFixedThreadPool(10);
    }
}
