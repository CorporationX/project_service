package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TeamRole;

import java.util.List;

public record StageFilterDto(
        List<TeamRole> roles,
        String taskStatus
) {
}
