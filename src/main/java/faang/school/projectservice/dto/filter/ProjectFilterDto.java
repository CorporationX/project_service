package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFilterDto {
    @NotBlank
    @Size(max = 255)
    private String namePattern;
    private ProjectStatus status;
}
