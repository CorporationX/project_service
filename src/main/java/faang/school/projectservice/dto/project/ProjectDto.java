package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private ProjectStatus status;

    private ProjectVisibility visibility;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String coverImageId;

    private List<Long> teams;
}
