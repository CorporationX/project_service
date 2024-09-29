package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private Long size;
    private List<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private LocalDateTime createdAt;
    private Long createdById;
    private Long updatedById;
    private LocalDateTime updatedAt;
    private Long projectId;
}