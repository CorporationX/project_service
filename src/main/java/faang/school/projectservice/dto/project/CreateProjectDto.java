package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateProjectDto {

    @Length(max = 128, message = "field \"name\" must have no more than 128 characters")
    @NotBlank(message = "field \"name\" must not be blank")
    private String name;

    @Length(max = 4096, message = "field \"description\" must have no more than 4096 characters")
    private String description;

    //todo в будущем можно убрать, когда разберемся с UserContext
    @Positive(message = "field \"ownerId\" must be positive")
    private Long ownerId;

    @NotNull(message = "field \"status\" must not be null")
    private ProjectStatus status = ProjectStatus.CREATED;

    @NotNull(message = "field \"visibility\" must not be null")
    private ProjectVisibility visibility = ProjectVisibility.PUBLIC;
}
