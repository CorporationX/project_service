package faang.school.projectservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectFilterDto {
    private String projectStatus;
    @Size(max = 255, message = "Project name should not exceed 255 characters")
    private String projectName;
    private String projectTask;
}
