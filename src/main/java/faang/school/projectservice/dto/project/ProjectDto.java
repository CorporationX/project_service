package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.Project;
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
    private String name;
    private String description;
    private Long ownerId;
    private Project parentProject;
    private List<ProjectDto> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
