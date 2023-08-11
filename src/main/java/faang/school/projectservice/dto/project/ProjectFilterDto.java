package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFilterDto {
    @Size(max = 255, message = "Project name pattern length can't be more than 255 symbols")
    private String projectNamePattern;
    private ProjectStatus status;
}
