package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResourceResponseDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private Long createdById;
    private LocalDateTime createdAt;
    private Long updatedById;
    private LocalDateTime updatedAt;
    private Long projectId;
}
