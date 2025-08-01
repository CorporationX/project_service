package faang.school.projectservice.dto.client.task;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

/**
 * DTO для отображение задачи пользователю
 *
 * @param name - название задачи
 * @param description - описание задачи
 * @param status - статус задачи
 * @param performerUserId - идентификатор исполнителя задачи
 * @param reporterUserId - идентификатор ревьюера задачи
 * @param minutesTracked - время на выполнения задачи
 * @param parentTaskId - идентификатор родительской задачи
 * @param linkedTasksId - список идентификаторов связанных задач
 * @param projectId - идентификатор проекта, которому принадлежит задача
 * @param stageId - идентификатор этапа
 * @author mrnght
 * @since 01.08.2025
 */
public record TaskViewDto(
        String name,
        String description,
        TaskStatus status,
        Long performerUserId,
        Long reporterUserId,
        Integer minutesTracked,
        Long parentTaskId,
        List<Long> linkedTasksId,
        Long projectId,
        Long stageId
) {
}
