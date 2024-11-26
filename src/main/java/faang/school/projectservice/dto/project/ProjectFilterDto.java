package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
public class ProjectFilterDto {
    @NotBlank(message = "Project name must not be empty.")
    @Size(min = 3, max = 128, message = "Name must be between 3 and 128 characters.")
    private String name;

    private ProjectStatus status;

    @Size(max = 255, message = "Pattern must not exceed 255 characters.")
    private String requestFilterPattern;
}
