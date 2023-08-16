package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUpdateDto {
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    @NotNull
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenId;
    private List<Long> tasksId;
    private List<Long> resourcesId;
    private ProjectVisibility visibility;
    private ProjectStatus status;
    private String coverImageId;
    private List<Long> teamsId;
    private Long scheduleId;
    private List<Long> stagesId;
    private List<Long> vacanciesId;
}
