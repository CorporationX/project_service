package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

    private Long id;

    @NotBlank
    @Size(max = 128)
    private String name;

    @Size(max = 4096)
    private String description;

    private Long ownerId;

    private ProjectStatus status;

    private ProjectVisibility visibility;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String coverImageId;

    private List<Long> teams;
}