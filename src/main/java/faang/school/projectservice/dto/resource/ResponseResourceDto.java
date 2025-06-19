package faang.school.projectservice.dto.resource;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ResponseResourceDto {
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
