package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для параметров фильтрации задач для их дальнейшего получения
 *
 * @param projectId - идентификатор проекта, которому принадлежит задача
 * @param status - статус задачи
 * @param performerUserId - идентификатор исполнителя задачи
 * @param name - название задачи
 * @author mrnght
 * @since 01.08.2025
 */
public record TaskFilterDto(
        @NotNull
        Long projectId,
        TaskStatus status,
        Long performerUserId,
        String name
) {
}
