package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private String description;
    private Long ownerId;
    private ProjectStatus status;
    private LocalDateTime updatedAt;
    private ProjectVisibility visibility;
}