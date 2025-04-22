package faang.school.projectservice.dto.jira.response;

import faang.school.projectservice.dto.project.ProjectStatusDto;
import faang.school.projectservice.dto.project.ProjectVisibilityDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private LocalDateTime createdAt;
    private ProjectStatusDto status;
    private ProjectVisibilityDto visibility;
    private String jiraKey;
}
