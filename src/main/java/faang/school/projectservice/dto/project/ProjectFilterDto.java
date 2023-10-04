package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFilterDto {
    @NotBlank
    @Size(max = 128, message = "Project's name patten length can't be more than 128 symbols")
    private String namePattern;
    private List<ProjectStatus> statuses;
}
