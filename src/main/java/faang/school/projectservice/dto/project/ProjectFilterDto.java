package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectFilterDto {
    private String name;

    private ProjectStatus status;

    @NotNull
    private Long userId;
}
