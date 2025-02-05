package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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