package faang.school.projectservice.dto.project;


import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProjectDto {

    private Long id;
    @NotBlank(message = "Name should not be empty.")
    private String name;
    @NotBlank(message = "Description should not be empty.")
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
