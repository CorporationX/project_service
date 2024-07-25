package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TaskStatusFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getTaskStatusPattern() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stageStream, StageFilterDto stageFilterDto) {
        return stageStream
                .filter(filter -> filter.getTasks()
                        .stream()
                        .anyMatch(task -> task.getStatus().equals(stageFilterDto.getTaskStatusPattern())));
    }
}
