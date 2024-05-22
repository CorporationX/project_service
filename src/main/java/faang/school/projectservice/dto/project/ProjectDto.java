package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDto {

    private Long id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    private String description;
    private Long parentId;

    @NotBlank(message = "Id владельца не должно быть пустым")
    private Long ownerId;
    private List<Long> childrenIds;
    private ProjectVisibility visibility;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}