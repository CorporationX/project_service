package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.*;
import faang.school.projectservice.validation.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    @NotNull(message = "Project's 'id' can not be null", groups = {ValidationGroups.Update.class})
    @Positive(message = "Project's 'id' should be greater than zero", groups = {ValidationGroups.Update.class})
    private Long id;
    @NotBlank(message = "Project's 'name' can not be empty", groups = {ValidationGroups.Create.class})
    private String name;
    @Size(max = 255, message = "Project's 'description' can not be greater than 255 symbols.",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @NotNull(message = "Project's 'description' can not be null", groups = {ValidationGroups.Create.class})
    private String description;
    @Positive(message = "parentProjectId должен быть положительным числом")
    private Long parentProjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    @NotNull(message = "Project 'visibility' can not be null", groups = {ValidationGroups.Create.class})
    private ProjectVisibility visibility;
    private List<Long> children;
    private Long ownerId;

}
