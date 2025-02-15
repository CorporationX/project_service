package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageStatusFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getTaskStatus() != null;
    }

    @Override
    public boolean filterEntity(Stage stage, StageFilterDto filters) {
        List<Task> tasks = stage.getTasks();

        return tasks.stream()
                .map(Task::getStatus)
                .anyMatch(taskStatus -> taskStatus.equals(filters.getTaskStatus()));
    }
}
