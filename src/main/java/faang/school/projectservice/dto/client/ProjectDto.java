package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProjectDto {
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private Long id;
}
