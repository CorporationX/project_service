package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.util.project.TaskUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class TaskKeywordFilter implements Filter<Task, TaskFilterDto> {

    private final TaskUtil taskUtil;

    @Override
    public boolean isApplicable(TaskFilterDto dto) {
        return dto.name() != null;
    }

    @Override
    public Stream<Task> filter(Stream<Task> entities, TaskFilterDto dto) {
        return entities.filter(task -> task.getName().toLowerCase().contains(dto.name().toLowerCase()))
                .filter(task -> taskUtil.isInTeam(task.getProject().getId()));
    }
}
