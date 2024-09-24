package faang.school.projectservice.filter;

import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;

import java.util.stream.Stream;

public class TeamRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getTeamRole() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filters) {
        return stages.filter(stage ->
                stage.getStageRoles().stream()
                        .anyMatch(stageRole -> stageRole.getTeamRole().equals(filters.getTeamRole())));
    }
}
