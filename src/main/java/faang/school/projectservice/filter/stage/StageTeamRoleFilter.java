package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StageTeamRoleFilter implements StageFilter {
    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.getRole() != null;
    }

    @Override
    public boolean filterEntity(Stage stage, StageFilterDto filters) {
        List<StageRoles> stageRoles = stage.getStageRoles();
        return stageRoles.stream()
                .map(StageRoles::getTeamRole)
                .anyMatch(filters.getRole()::contains);

    }
}
