package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class SubProjectDto {
    Long id;
    @NotBlank
    String name;
    String description;
    @NotNull
    Long parentProjectId;
    List<Long> childIds;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    ProjectStatus status;
    ProjectVisibility visibility;
    List<Long> stageStageIds;
    List<Long> vacancyIds;
    List<Long> momentIds;
}