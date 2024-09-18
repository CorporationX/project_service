package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ProjectDto {
    private long id;
    @NotEmpty
    @Size(max = 128, message = "Name must be less than 128 characters")
    private String name;
    @NotEmpty
    @Size(max = 4096, message = "Description must be less than 4096 characters")
    private String description;
    @Min(value = 1, message = "Owner id should not be less than 1")
    private long ownerId;
    private long parentProjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotNull
    private ProjectStatus status;
    @NotNull
    private ProjectVisibility visibility;
}