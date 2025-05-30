package faang.school.projectservice.dto;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResourceDto {
    private long id;
    private String name;
    private String key;
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private long createdById;
    private long updatedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long projectId;
}
