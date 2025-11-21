package faang.school.projectservice.dto;

import faang.school.projectservice.dto.simple.MeetSimpleDto;
import faang.school.projectservice.dto.simple.MomentSimpleDto;
import faang.school.projectservice.dto.simple.ProjectSimpleDto;
import faang.school.projectservice.dto.simple.ResourceSimpleDto;
import faang.school.projectservice.dto.simple.ScheduleSimpleDto;
import faang.school.projectservice.dto.simple.StageSimpleDto;
import faang.school.projectservice.dto.simple.TaskSimpleDto;
import faang.school.projectservice.dto.simple.TeamSimpleDto;
import faang.school.projectservice.dto.simple.VacancySimpleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private Long ownerId;
    private ProjectSimpleDto parentProject;
    private List<ProjectSimpleDto> children;
    private List<TaskSimpleDto> tasks;
    private List<ResourceSimpleDto> resources;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private faang.school.projectservice.model.ProjectStatus status;
    private faang.school.projectservice.model.ProjectVisibility visibility;
    private String coverImageId;
    private List<TeamSimpleDto> teams;
    private ScheduleSimpleDto schedule;
    private List<StageSimpleDto> stages;
    private List<VacancySimpleDto> vacancies;
    private List<MomentSimpleDto> moments;
    private List<MeetSimpleDto> meets;
    private String presentationFileKey;
    private LocalDateTime presentationGeneratedAt;
    private List<String> galleryFileKeys;
}