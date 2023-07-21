package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private TeamMember owner;
    private List<Long> tasksId;
    private List<Resource> resources;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private String coverImageId;
    private Team team;
    private Schedule schedule;
    private List<Stage> stages;
}
