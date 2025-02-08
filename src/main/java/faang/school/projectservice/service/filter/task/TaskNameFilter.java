package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TaskNameFilter implements TaskGetting {
    @Override
    public Stream<Task> filter(Stream<Task> stream, TaskGettingDto request) {
        return stream.filter(task -> task.getName().contains(request.word()));
    }

    @Override
    public boolean isApplicable(TaskGettingDto request) {
        return request.word() != null;
    }
}
