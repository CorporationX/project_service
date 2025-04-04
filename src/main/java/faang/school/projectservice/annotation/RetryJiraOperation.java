package faang.school.projectservice.annotation;

import faang.school.projectservice.exception.JiraConnectionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
        retryFor = JiraConnectionException.class,
        backoff = @Backoff(
                delayExpression = "${retry.default.initial-interval}",
                multiplierExpression = "${retry.default.multiplier}",
                maxDelayExpression = "${retry.default.max-interval}"
        )
)
public @interface RetryJiraOperation {
}
