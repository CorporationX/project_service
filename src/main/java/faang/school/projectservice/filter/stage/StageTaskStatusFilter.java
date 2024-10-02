package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public class StageTaskStatusFilter implements Filter<StageFilterDto, Stage> {
    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return !stageFilterDto.getStageRolesDtos().isEmpty() &&
                stageFilterDto.getTaskDtos().stream().anyMatch(taskDto -> taskDto.getStatus() != null);
    }

    @Override
    public Stream<Stage> applyFilter(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        return stages.filter(stage -> stage.getTasks().stream()
                .anyMatch(task -> stageFilterDto.getTaskDtos().stream()
                        .anyMatch(taskDto ->
                                task.getStatus().equals(taskDto.getStatus()))));
    }
}
