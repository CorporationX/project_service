package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {

    private long ProjectId;
    private long parentProjectId;
    private long ownerId;
    private String projectName;
    private String projectDescription;
    private ProjectStatus projectStatus;
    private ProjectVisibility projectVisibility;
    private LocalDateTime projectCreatedAt;
    private LocalDateTime projectUpdatedAt;
}
