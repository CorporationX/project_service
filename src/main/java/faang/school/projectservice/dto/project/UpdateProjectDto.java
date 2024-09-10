package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateProjectDto {

    @Positive(message = "field \"id\" must be positive")
    @NotNull(message = "field \"id\" must not be null")
    private Long id;

    @Length(max = 4096, message = "field \"description\" must have no more than 4096 characters")
    private String description;

    @NotNull(message = "field \"status\" must not be null")
    private ProjectStatus status = ProjectStatus.CREATED;
}
