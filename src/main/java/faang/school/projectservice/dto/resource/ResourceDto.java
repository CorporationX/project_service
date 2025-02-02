package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceDto {
    private Long id;
    private String name;
    private Long projectId;
    private ResourceType type;
    private ResourceStatus status;
    private TeamMemberDto createdBy;
    private TeamMemberDto updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
