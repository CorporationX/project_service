package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public record TeamMemberDto(
        Long id,
        String name,
        TeamRole role) {
}
