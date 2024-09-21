package faang.school.projectservice.service.project.stage.filters;

import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class RolesFilter implements StageFilter {

    @Override
    public boolean isApplicable(StageFilterDto filters) {
        return filters.roleFilter() != null;
    }

    @Override
    public Stream<Stage> apply(@NonNull Stream<Stage> stages, @NonNull StageFilterDto filters) {
        return stages
                .filter(stage -> stage.getStageRoles().stream()
                        .map(StageRoles::getTeamRole)
                        .anyMatch(role -> role.equals(filters.roleFilter())));
    }
}
