package faang.school.projectservice.dto.projectresource;

import faang.school.projectservice.model.TeamRole;
import java.util.List;
import lombok.Data;

@Data
public class ProjectResourceDto {
    private Long id;
    private String name;
    private String key;
    private List<TeamRole> allowedRoles;
    private Long createdById;
    private Long projectId;
}
