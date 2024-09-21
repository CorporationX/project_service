package faang.school.projectservice.dto.project;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {

    private long projectId;
    private long parentProjectId;
    private long ownerId;
    private String projectName;
    private String projectDescription;
    private ProjectStatus projectStatus;
    private ProjectVisibility projectVisibility;
    private LocalDateTime projectCreatedAt;
    private LocalDateTime projectUpdatedAt;
    private List<TeamDto> teamDtos;
}
