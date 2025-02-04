package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProjectUpdateRequestDto {
    @Positive
    private Long id;
    @NotNull
    private ProjectStatus status;
    @NotBlank
    private String description;
}
