package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDto {
    @Size(max = 255, message = "Name pattern should not exceed 255 characters")
    private String namePattern;
    private ProjectStatus statusPattern;
}