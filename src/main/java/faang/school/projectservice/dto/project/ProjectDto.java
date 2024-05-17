package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    @NonNull
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 1, max = 100)
    private String name;

    @NonNull
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    @NonNull
    @NotBlank(message = "Id владельца не должно быть пустым")
    private Long ownerId;

    private ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}