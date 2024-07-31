package faang.school.projectservice.dto.sharedfiles;

import faang.school.projectservice.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private List<TeamRole> allowedRoles;
    private ResourceType type;
    private ResourceStatus status;
    private TeamMember createdBy;
    private TeamMember updatedBy;
    private Project project;
}
