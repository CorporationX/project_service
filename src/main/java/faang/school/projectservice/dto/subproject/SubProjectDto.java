package faang.school.projectservice.dto.subproject;

import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.dto.validation.CreateGroup;
import faang.school.projectservice.dto.validation.UpdateGroup;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class SubProjectDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(groups = UpdateGroup.class, message = "ID can't be null when updating")
    @Positive(groups = UpdateGroup.class, message = "id must be greater than zero")
    private Long id;

    @NotBlank(groups = CreateGroup.class, message = "Project name can't be null or empty when creating")
    private String name;

    @NotBlank(groups = CreateGroup.class, message = "Project name can't be null or empty when creating")
    private String description;

    @NotNull(groups = CreateGroup.class, message = "OwnerId can't be null")
    @Positive(groups = CreateGroup.class, message = "OwnerId must be greater than 0")
    private Long ownerId;

    @NotNull(groups = CreateGroup.class, message = "parentProjectId can't be null when creating")
    @Positive(groups = CreateGroup.class, message = "parentProjectId must be greater than 0 when creating")
    private Long parentProjectId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updatedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(groups = UpdateGroup.class, message = "Status can't be null when updating")
    private ProjectStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(groups = UpdateGroup.class, message = "Visibility can't be null when updating")
    private ProjectVisibility visibility;
}