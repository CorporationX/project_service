package faang.school.projectservice.dto;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
