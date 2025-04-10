package faang.school.projectservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RetryJiraConfig {

    @Value("${retry.backoff.initial-interval}")
    private long initialInterval;

    @Value("${retry.backoff.multiplier}")
    private double multiplier;

    @Value("${retry.backoff.max-interval}")
    private long maxInterval;
}
