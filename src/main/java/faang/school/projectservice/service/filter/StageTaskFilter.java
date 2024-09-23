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
        switch (stageFilterDto.getRoleFilterEnum()) {
            case ANY -> {
                return stageStream.filter(stage ->
                        stage.getTasks().stream().anyMatch(task -> task.getStatus().equals(
                                stageFilterDto.getStatus()))
                );
            }
            case ALL -> {
                return stageStream.filter(stage ->
                        stage.getTasks().stream().allMatch(task -> task.getStatus().equals(
                                stageFilterDto.getStatus()))
                );
            }
            case NOTHING -> {
                return stageStream.filter(stage ->
                        stage.getTasks().stream().noneMatch(task -> task.getStatus().equals(
                                stageFilterDto.getStatus()))
                );
            }
            default -> {
                return stageStream;
            }
        }
    }
}