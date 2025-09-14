package faang.school.projectservice.config.retry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Конфигурация механизма повторных попыток (retry) для приложения.
 *
 * @author Linempy
 * @since 28.08.2025
 */
@Configuration
public class RetryConfig {

    @Value("${retry.config.max-attempts}")
    private int maxAttempts;

    @Value("${retry.config.initial-interval-ms}")
    private int initialInterval;

    @Value("${retry.config.multiplier}")
    private double multiplier;

    @Value("${retry.config.max-interval-ms}")
    private int maxInterval;

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialInterval);
        backOffPolicy.setMultiplier(multiplier);
        backOffPolicy.setMaxInterval(maxInterval);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}