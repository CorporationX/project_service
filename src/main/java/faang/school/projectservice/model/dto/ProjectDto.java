package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.ProjectStatus;
import faang.school.projectservice.model.enums.ProjectVisibility;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private Long parentProjectId;
    private Long ownerId;
    private List<Long> childrenIds;
    private String coverImageId;
}
