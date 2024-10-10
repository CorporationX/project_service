package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResourceDto {
    private final Long id;
    private final String key;
    private final String name;
    private final BigInteger size;
    private final List<TeamRole> allowedRoles;
    private final ResourceType type;
    private final ResourceStatus status;
    private final Long createdById;
    private final Long updatedById;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long projectId;
}