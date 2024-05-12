package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    @NonNull
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @NonNull
    @NotBlank
    private String description;

    @NonNull
    @NotBlank
    private Long ownerId;

    private ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
