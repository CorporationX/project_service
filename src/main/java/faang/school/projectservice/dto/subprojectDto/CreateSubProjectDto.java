package faang.school.projectservice.dto.subprojectDto;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    @NotNull
    private Long parentProjectId;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private ProjectStatus status;
}
