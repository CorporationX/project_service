package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProjectDto {

    private long id;
    private String name;
    private String description;
    private long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;

}
