package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageTeamRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filter) {
        return filter.getTeamRole() != null;
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto filter) {
        return stages.filter((stage -> stage.getStageRoles().stream()
                .anyMatch(role -> role.getTeamRole().equals(filter.getTeamRole()))));
    }
}