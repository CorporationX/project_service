package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.util.task.TaskUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class TaskPerformerFilter implements Filter<Task, TaskFilterDto> {

    private final TaskUtil taskUtil;

    @Override
    public boolean isApplicable(TaskFilterDto dto) {
        return dto.performerUserId() != null;
    }

    @Override
    public Stream<Task> filter(Stream<Task> entities, TaskFilterDto dto) {
        return entities.filter(task -> task.getPerformerUserId().equals(dto.performerUserId()))
                .filter(task -> taskUtil.isInTeam(task.getProject().getId()));
    }
}
