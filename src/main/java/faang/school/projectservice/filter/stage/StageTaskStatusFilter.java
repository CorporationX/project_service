package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageTaskStatusFilter implements Filter<Stage, StageFilterDto> {
    @Override
    public boolean isApplicable(StageFilterDto filter) {
        return filter.getTaskStatus() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> dataStream, StageFilterDto filter) {
        return dataStream.filter(stage -> stage.getTasks().stream()
                .anyMatch(task -> task.getStatus() == (filter.getTaskStatus())));
    }
}
