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
    private Long id;
    private String key;
    private String name;
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private Long createdById;
    private Long updatedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
}
