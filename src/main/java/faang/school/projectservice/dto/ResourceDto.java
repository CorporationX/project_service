package faang.school.projectservice.dto;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDto {
    Long id;
    String name;
    String key;
    BigInteger size;
    List<TeamRole> allowedRoles;
    ResourceType type;
    ResourceStatus status;
    LocalDateTime createdAt;
    TeamMemberDto createdBy;
    TeamMemberDto updatedBy;
    LocalDateTime updatedAt;
    ProjectDto project;
}
