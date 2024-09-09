package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;

import java.util.List;

public record TeamMemberDto(Long id, List<TeamRole> stageRoles) {
}
