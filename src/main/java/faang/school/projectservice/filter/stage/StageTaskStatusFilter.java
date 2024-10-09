package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public class StageTaskStatusFilter implements Filter<StageFilterDto, Stage> {
    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getTaskStatuses() != null &&
                !stageFilterDto.getTaskStatuses().isEmpty();

    }

    @Override
    public Stream<Stage> applyFilter(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> stage.getTasks().stream()
                .anyMatch(task -> stageFilterDto.getTaskStatuses().contains(task.getStatus())));
    }
}
