package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageStatusFilter implements StageFilter{
    @Override
    public boolean isApplicable(StageFilterDto filter) {
        return filter.getTaskStatus() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stageStream, StageFilterDto filter) {
        return stageStream
                .filter(stage -> stage.getTasks().stream().anyMatch(task -> task.getStatus().equals(filter.getTaskStatus())));
    }
}