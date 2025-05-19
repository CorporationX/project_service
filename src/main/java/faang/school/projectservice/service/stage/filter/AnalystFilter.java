package faang.school.projectservice.service.stage.filter;

import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class AnalystFilter implements RoleFilter {
    @Override
    public boolean filter(TeamRole role) {
        return role==TeamRole.ANALYST;
    }
}
