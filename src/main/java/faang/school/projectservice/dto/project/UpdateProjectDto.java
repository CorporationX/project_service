package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
public class UpdateProjectDto {
    @NotNull(message = "ProjectId is required.")
    @Positive(message = "ProjectId must be greater than 0.")
    private Long id;

    @NotBlank(message = "Project name must not be empty.")
    @Size(min = 3, max = 128, message = "Name must be between 3 and 128 characters.")
    private String name;

    @NotNull(message = "OwnerId is required.")
    @Positive(message = "OwnerId must be greater than 0.")
    private Long ownerId;

    @Size(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    private String description;

    @Size(max = 255, message = "Pattern must not exceed 255 characters.")
    private String requestFilterPattern;

    private ProjectVisibility visibility;
    private ProjectStatus status;
}
