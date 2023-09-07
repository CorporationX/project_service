package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class ProjectDto {

    @NotNull
    private Long id;

    @NotEmpty
    @Size(max = 128, message = "Name must be less than 128 characters")
    private String name;

    @NotEmpty
    @Size(max = 4096, message = "Name must be less than 4096 characters")
    private String description;

    @NotNull
    private Long ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    private ProjectStatus status;

    private ProjectVisibility visibility;
}
