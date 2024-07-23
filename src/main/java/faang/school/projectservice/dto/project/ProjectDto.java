package faang.school.projectservice.dto.project;


import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private Long ownerId;
    private ProjectDto parentProject;
    private List<ProjectDto> children;
    private List<Long> resourceIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private String coverImageId;
    private List<TeamDto> teams;
    private List<Long> momentIds;
}
