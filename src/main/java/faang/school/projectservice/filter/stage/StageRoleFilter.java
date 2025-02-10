package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getTeamRole() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        TeamRole teamRole = filters.getTeamRole();
        return stages.filter(stage -> stage.getStageRoles()
                .stream()
                .map(StageRoles::getTeamRole)
                .anyMatch(teamRole::equals));
    }
}
