package faang.school.projectservice.publisher;

import faang.school.projectservice.exception.EventPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Универсальный компонент для безопасной публикации событий после коммита транзакции
 * с поддержкой повторных попыток
 *
 * @param <E> тип события
 * @author Linempy
 * @since 27.08.2025
 */

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<E> implements EventPublisher<E> {

    protected final RetryTemplate retryTemplate;
    protected final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(E event) {
        String topic = getTopic();

        try {
            Long receiversCount = redisTemplate.convertAndSend(topic, event);

            if (receiversCount != null && receiversCount > 0) {
                log.info("Событие успешно отправлено в топик {}. Получателей: {}",
                        topic, receiversCount);
            } else {
                log.warn("Событие отправлено в топик {}, но нет активных подписчиков",
                        topic);
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке события в топик {}: {}",
                    topic, event, e);
            throw new EventPublishingException("Ошибка отправки ивента в Redis", e);
        }
    }

    public void publishAfterCommit(E event) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            retryTemplate.execute(context -> {
                                publish(event);
                                return null;
                            });
                        } catch (EventPublishingException e) {
                            log.error("Ошибка отправки ивента после повторной отправки: {}", event, e);
                        }
                    }
                }
        );
    }

    protected abstract String getTopic();
}