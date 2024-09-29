package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.util.List;

@Data
public class ResourceUpdateDto {
    private Long id;
    private String name;
    private List<TeamRole> allowedRoles;
    private ResourceStatus status;
    private Long updatedById;
}
