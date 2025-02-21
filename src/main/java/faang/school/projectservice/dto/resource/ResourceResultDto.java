package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public record ResourceResultDto(
        Long id,
        String name,
        String key,
        BigInteger size,
        List<TeamRole> allowedRoles,
        ResourceType type,
        ResourceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
