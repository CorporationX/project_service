package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TeamRole;

public record TeamMemberDto(Long id,
                            String name,
                            TeamRole role) {
}
