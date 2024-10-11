package faang.school.projectservice.model.filter.stage;

import faang.school.projectservice.model.dto.stage.StageFilterDto;
import faang.school.projectservice.model.entity.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TaskStatusFilter implements StageFilter {

    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.taskStatus() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        return stages.filter(stage -> stage.getTasks()
                .stream()
                .anyMatch(task -> task.getStatus() == filters.taskStatus()));
    }
}
