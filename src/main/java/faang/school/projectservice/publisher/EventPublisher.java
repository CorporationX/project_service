package faang.school.projectservice.publisher;


import faang.school.projectservice.exception.EventPublishingException;

/**
 * Универсальный интерфейс для публикации событий любого типа
 *
 * @param <E> тип события
 *
 * @author Linempy
 * @since 27.08.2025
 */
public interface EventPublisher<E> {
    void publish(E event) throws EventPublishingException;
}