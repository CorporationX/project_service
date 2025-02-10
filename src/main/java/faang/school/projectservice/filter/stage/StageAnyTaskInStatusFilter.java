package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class StageAnyTaskInStatusFilter extends AbstractStageStatusFilter {
    @Override
    protected TaskStatus getTaskStatus(StageFilterDto filters) {
        return filters.getAnyTaskInStatus();
    }

    @Override
    protected Function<Stream<TaskStatus>, Boolean> getMatchingStrategy(TaskStatus taskStatus) {
        return statuses -> statuses.anyMatch(taskStatus::equals);
    }
}
