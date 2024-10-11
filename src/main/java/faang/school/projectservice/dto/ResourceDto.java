package faang.school.projectservice.dto;

import faang.school.projectservice.model.ResourceStatus;
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
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private String type;
    private ResourceStatus status;
    private LocalDateTime createdAt;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private Long projectId;
}