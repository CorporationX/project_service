package faang.school.projectservice.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TaskStatusFilter implements StageFilter {

    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.taskStatusFilter() != null && filters.taskStatusFilterType() != null;
    }

    @Override
    public Stream<Stage> apply(@NonNull Stream<Stage> stages, @NonNull StageFilterDto filters) {
        return switch (filters.taskStatusFilterType()) {
            case ALL -> filterByAllMatchWithStatus(stages, filters.taskStatusFilter());
            case ANY -> filterByAnyMatchWithStatus(stages, filters.taskStatusFilter());
            case NONE -> filterByNoneMatchWithStatus(stages, filters.taskStatusFilter());
        };
    }

    private Stream<Stage> filterByAnyMatchWithStatus(Stream<Stage> stages, TaskStatus status) {
        return stages.filter(stage -> stage.getTasks().stream()
                .anyMatch(task -> task.getStatus().equals(status)));
    }

    private Stream<Stage> filterByAllMatchWithStatus(Stream<Stage> stages, TaskStatus status) {
        return stages.filter(stage -> stage.getTasks().stream()
                .allMatch(task -> task.getStatus().equals(status)));
    }

    private Stream<Stage> filterByNoneMatchWithStatus(Stream<Stage> stages, TaskStatus status) {
        return stages.filter(stage -> stage.getTasks().stream()
                .noneMatch(task -> task.getStatus().equals(status)));
    }
}