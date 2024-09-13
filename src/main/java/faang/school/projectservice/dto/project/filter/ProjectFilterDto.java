package faang.school.projectservice.dto.project.filter;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectFilterDto {
    @Size(max = 255)
    private String name;
    private ProjectStatus status;
}
