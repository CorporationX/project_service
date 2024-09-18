package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubProjectDto {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 128)
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 4096)
    private String description;

    @Min(1)
    private long ownerId;

    @Min(1)
    private long parentProjectId;

    @NotNull
    private ProjectVisibility visibility;
}
