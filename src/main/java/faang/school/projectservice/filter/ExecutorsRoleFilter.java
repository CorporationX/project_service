package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ExecutorsRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filterDto) {
        return filterDto.getRole() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stageStream, StageFilterDto stageFilterDto) {
        return stageStream
                .filter(stage -> stage.getStageRoles().stream()
                        .map(StageRoles::getTeamRole)
                        .anyMatch(role -> role.equals(stageFilterDto.getRole()))
                );
    }
}
