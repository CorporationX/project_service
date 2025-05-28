package faang.school.projectservice.filter.stage;

import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class AnalystFilter implements RoleFilter {
    @Override
    public boolean filter(TeamRole role) {
        return role == TeamRole.ANALYST;
    }
}
