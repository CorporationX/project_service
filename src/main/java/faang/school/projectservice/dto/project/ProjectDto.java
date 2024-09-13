package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {

    private long projectOwnerId;
    private long ownerId;
    private String projectName;
    private String projectDescription;
    private ProjectStatus projectStatus;
    private ProjectVisibility projectVisibility;
    private LocalDateTime projectCreatedAt;
    private LocalDateTime projectUpdatedAt;
    private List<Team> teams;
}
