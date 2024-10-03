package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ResourceUpdateDto {
    private Long id;
    private String name;
    private List<TeamRole> allowedRoles;
    private ResourceStatus status;
    private Long updatedById;
}
