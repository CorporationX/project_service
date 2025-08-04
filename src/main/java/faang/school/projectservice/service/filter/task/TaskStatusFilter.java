package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.service.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * TaskStatusFilter — описание класса.
 * <p>
 * Возвращает отфильтрованные по статусу{@link faang.school.projectservice.model.TaskStatus} задачи
 * </p>
 *
 * @author mrnght
 * @since 01.08.2025
 */
@Component
@RequiredArgsConstructor
public class TaskStatusFilter implements Filter<Task, TaskFilterDto> {

    @Override
    public boolean isApplicable(TaskFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Stream<Task> filter(Stream<Task> entities, TaskFilterDto dto) {
        return entities.filter(task -> task.getStatus().equals(dto.status()));
    }
}
