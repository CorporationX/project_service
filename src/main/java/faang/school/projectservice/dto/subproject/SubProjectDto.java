package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record SubProjectDto(

    @NotBlank(message = "Subproject name can not be null or empty")
    @Size(max = 128)
    String name,

    @NotBlank(message = "Subproject description can not be null or empty")
    @Size(max = 4096)
    String description,

    @Positive
    Long ownerId,

    @Positive
    Long parentProjectId,

    @NotNull
    ProjectVisibility visibility
) {}
