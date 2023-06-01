package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.Data;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long owner;
    private ProjectStatus status;
}