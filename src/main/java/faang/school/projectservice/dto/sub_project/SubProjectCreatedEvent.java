package faang.school.projectservice.dto.sub_project;

/**
 * Класс-ивент для уведомления о создании подпроекта
 *
 * @author Linempy
 * @since 23.08.2025
 */
public record SubProjectCreatedEvent(
        Long parentId,
        Long id,
        Long ownerId
) {
}