package faang.school.projectservice.serivce.project.stage.filters;

import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TaskStatusFilter implements StageFilter {

    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.taskStatusFilter() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        return switch (filters.taskStatusFilter()) {
            case TODO -> filterByAnyMatchWithStatus(stages, TaskStatus.TODO);
            case IN_PROGRESS -> filterByAnyMatchWithStatus(stages, TaskStatus.IN_PROGRESS);
            case REVIEW -> filterByAnyMatchWithStatus(stages, TaskStatus.REVIEW);
            case TESTING -> filterByAnyMatchWithStatus(stages, TaskStatus.TESTING);
            case DONE -> filterByAllMatchWithStatus(stages, TaskStatus.DONE);
            case CANCELLED -> filterByAllMatchWithStatus(stages, TaskStatus.CANCELLED);
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
}
