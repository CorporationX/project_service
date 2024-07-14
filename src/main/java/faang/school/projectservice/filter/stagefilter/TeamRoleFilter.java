package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.client.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TeamRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto.getTeamRolePattern() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stageStream, StageFilterDto stageFilterDto) {
        return stageStream
                .filter(filter -> filter.getStageRoles()
                        .stream()
                        .anyMatch(role -> role.getTeamRole().equals(stageFilterDto.getTeamRolePattern())));

    }
}
