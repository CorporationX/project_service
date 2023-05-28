package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private TeamMember owner;
    private List<TaskDto> tasks;
    private List<ResourceDto> resources;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private String coverImageId;
    private TeamDto team;
    private ScheduleDto schedule;
}