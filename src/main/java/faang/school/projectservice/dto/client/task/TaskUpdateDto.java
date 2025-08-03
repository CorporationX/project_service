package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO для обновления задачи.
 *
 * @param name - название задачи
 * @param description - описание задачи
 * @param status - статус задачи
 * @param performerUserId - идентификатор исполнителя задачи
 * @param reporterUserId - идентификатор ревьюера задачи
 * @param minutesTracked - время на выполнения задачи
 * @param linkedTasksId - список идентификаторов связанных задач
 * @param projectId - идентификатор проекта, которому принадлежит задача
 * @param stageId - идентификатор этапа
 * @author mrnght
 * @since 01.08.2025
 */
public record TaskUpdateDto(
        @NotNull
        String name,
        @NotNull
        String description,
        @NotNull
        TaskStatus status,
        @NotNull
        Long performerUserId,
        @NotNull
        Long reporterUserId,
        @NotNull
        Integer minutesTracked,
        @NotNull
        List<Long> linkedTasksId,
        @NotNull
        Long projectId,
        @NotNull
        Long stageId
) {
}
