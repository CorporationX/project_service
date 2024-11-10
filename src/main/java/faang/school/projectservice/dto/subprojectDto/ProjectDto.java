package faang.school.projectservice.dto.subprojectDto;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private ProjectStatus status;
    private List<StageDto> stages;
}
