package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import faang.school.projectservice.dto.groups.Groups;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ProjectDto {

    @NotNull(groups = Groups.OnUpdate.class, message = "id is required on update")
    @Null(groups = Groups.OnCreate.class, message = "id must be null on create")
    @Positive(message = "id must be positive")
    private Long id;

    @Length(max = 128, message = "name must not contain more than 128 characters")
    @NotBlank(groups = Groups.OnCreate.class, message = "project name cannot be blank")
    private String name;

    @Length(max = 4096, message = "description must not contain more than 4096 characters")
    @NotNull(groups = Groups.OnCreate.class, message = "project description must not be null on create")
    private String description;

    @Null(groups = Groups.OnCreate.class, message = "status must be null on create")
    private ProjectStatus status;

    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;

    @Positive(message = "owner id must be positive")
    private Long ownerId;

    @JsonIgnore
    @AssertTrue(groups = Groups.OnUpdate.class, message = "project description or project status is required on update")
    public boolean isValidOnUpdate() {
        return description != null || status != null;
    }
}
