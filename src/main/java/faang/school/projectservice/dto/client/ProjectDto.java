package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {

    @NotBlank(message = "Name should not be blank")
    private String name;

    @NotBlank(message = "description should not be blank")
    @Size(min = 1, max = 255, message = "Description must be between 1 and 255 characters")
    private String description;

    @NotNull(message = "Status should not be null")
    private ProjectStatus status;
  
    @NotNull(message = "Visibility should not be null")
    private ProjectVisibility visibility;

    @Positive(message = "ID should not be null")
    private Long id;

    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String jiraKey;
}
