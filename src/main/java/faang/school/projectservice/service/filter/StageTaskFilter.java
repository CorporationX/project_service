package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageTaskFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filterDto) {
        return filterDto.getRoleFilterEnum() != null && filterDto.getStatus() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stageStream, StageFilterDto stageFilterDto) {
        if (stageFilterDto.getRoleFilterEnum() == TaskFilterEnum.ANY) {
            return stageStream.filter( stage ->
                    stage.getTasks().stream().anyMatch(task -> task.getStage().equals(stage))
            );
        } else if (stageFilterDto.getRoleFilterEnum() == TaskFilterEnum.ALL){
            return stageStream.filter( stage ->
                    stage.getTasks().stream().allMatch(task -> task.getStage().equals(stage))
            );
        } else {
            return stageStream.filter( stage ->
                    stage.getTasks().stream().noneMatch(task -> task.getStage().equals(stage))
            );
        }
    }
}
