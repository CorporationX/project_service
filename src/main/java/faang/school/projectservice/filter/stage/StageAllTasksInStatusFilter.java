package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class StageAllTasksInStatusFilter extends AbstractStageStatusFilter {
    @Override
    protected TaskStatus getTaskStatus(StageFilterDto filters) {
        return filters.getAllTasksInStatus();
    }

    @Override
    protected Function<Stream<TaskStatus>, Boolean> getMatchingStrategy(TaskStatus taskStatus) {
        return statuses -> statuses.allMatch(taskStatus::equals);
    }
}
