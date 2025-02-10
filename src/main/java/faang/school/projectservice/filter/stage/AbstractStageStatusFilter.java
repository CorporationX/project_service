package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class AbstractStageStatusFilter implements StageFilter {

    protected abstract TaskStatus getTaskStatus(StageFilterDto filters);

    protected abstract Function<Stream<TaskStatus>, Boolean> getMatchingStrategy(TaskStatus taskStatus);

    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return getTaskStatus(filters) != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        return stages.filter(stage -> getMatchingStrategy(getTaskStatus(filters)).apply(stage.getTasks()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(Task::getStatus)));
    }
}
