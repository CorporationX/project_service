package faang.school.projectservice.filter.stage;

import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class ManagerFilter implements RoleFilter {
    @Override
    public boolean filter(TeamRole role) {
        return role == TeamRole.MANAGER;
    }
}
