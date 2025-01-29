package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;

    @NotBlank(message = "name must be fielded")
    private String name;

    @NotBlank(message = "description must be fielded")
    private String description;

    private Long ownerId;

    private ProjectStatus status;

    private ProjectVisibility visibility;

    @JsonFormat(pattern = "yyyy-mm-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-mm-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
