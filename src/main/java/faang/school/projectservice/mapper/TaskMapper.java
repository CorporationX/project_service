package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.task.TaskCreateDto;
import faang.school.projectservice.dto.client.task.TaskUpdateDto;
import faang.school.projectservice.dto.client.task.TaskViewDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * TaskMapper — для преобразования между сущностью {@link Task} и DTO.
 * <p>
 * Представляет методы для конвертации данных.
 * </p>*
 *
 * @author mrnght
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    /**
     * Преобразует DTO создания задачи в сущность {@link Task}.
     */
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Task toEntity(TaskCreateDto taskDto);

    /**
     * Обновляет DTO задачи в сущность {@link Task}.
     */
    void update(TaskUpdateDto taskDto, @MappingTarget Task entity);

    /**
     * Преобразует сущность {@link Task} в DTO для отображения.
     */
    @Mapping(target = "linkedTasksId", expression = "java(getLinkedTaskIds(task.getLinkedTasks()))")
    @Mapping(target = "parentTaskId", source = "task.parentTask.id")
    @Mapping(target = "projectId", source = "task.project.id")
    @Mapping(target = "stageId", source = "task.stage.stageId")
    TaskViewDto toViewDto(Task task);

    /**
     * Конвертирует список связанных задач в список их id
     * @param tasks - список связанных задач
     * @return - список id связанных задач
     */
    default List<Long> getLinkedTaskIds(List<Task> tasks) {
        if (tasks == null) {
            return new ArrayList<>();
        }
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }
}
