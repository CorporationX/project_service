package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SubProjectUpdateDto {
    @NotNull
    @Positive
    private Long id;
    @NotNull
    private ProjectStatus projectStatus;
    private ProjectVisibility visibility;
    private LocalDateTime updatedAt;
}
