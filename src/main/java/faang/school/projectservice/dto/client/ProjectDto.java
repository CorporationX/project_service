package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectDto {
    @NotNull(message = "project id cannot be null!")
    private Long id;
    private String title;
}
