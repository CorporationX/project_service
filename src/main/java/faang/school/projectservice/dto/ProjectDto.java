package faang.school.projectservice.dto;

import faang.school.projectservice.model.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ProjectDto {
    private Long id;
    @NotBlank(message = "Name should not be empty.")
    private String name;
    @NotBlank(message = "Description should not be empty.")
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private List<Long> moments;
}
