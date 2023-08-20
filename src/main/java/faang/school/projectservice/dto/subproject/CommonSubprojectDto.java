package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.*;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonSubprojectDto {
    private Long subprojectId;

    private String name;

    private String description;

    private BigInteger storageSize;

    private BigInteger maxStorageSize;

    private Long ownerId;

    private Long parentProjectId;

    private List<Long> childrenIds;

    private List<Long> tasksIds;

    private List<Long> resourcesIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ProjectStatus status;

    private ProjectVisibility visibility;

    private String coverImageId;

    private List<Long> teamsIds;

    private Schedule schedule;

    private List<Long> stagesIds;

    private List<Long> vacanciesIds;

    private List<Long> momentsIds;
}
