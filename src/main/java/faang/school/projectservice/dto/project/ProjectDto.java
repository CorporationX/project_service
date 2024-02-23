package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validation.ValidationGroups;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    @NotNull(message = "Project 'id' can not be null", groups = {ValidationGroups.Update.class})
    @Min(value = 1, message = "Project 'id' should be greater than zero", groups = {ValidationGroups.Update.class})
    private Long id;
    @NotNull(message = "Project 'name' can not be null", groups = {ValidationGroups.Create.class})
    private String name;
    @NotNull(message = "Project 'description' can not be null", groups = {ValidationGroups.Create.class})
    private String description;
    private Long ownerId;
    private ProjectStatus status;
    @NotNull(message = "Project 'visibility' can not be null", groups = {ValidationGroups.Create.class})
    private ProjectVisibility visibility;

}
