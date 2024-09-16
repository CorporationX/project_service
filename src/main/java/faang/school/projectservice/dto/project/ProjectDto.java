package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ProjectDto {
    @Positive(message = "id must be positive")
    private Long id;
    @Length(max = 128, message = "name must not contain more than 128 characters")
    private String name;
    @Length(max = 4096, message = "description must not contain more than 4096 characters")
    private String description;
    private ProjectStatus status = ProjectStatus.CREATED;
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    @Positive(message = "owner id must be positive")
    private Long ownerId;
}
