package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectUpdateDto {
    @Size(max = 4096, message = "Описание проекта не должно превышать 4096 символов!")
    private String description;
    private ProjectStatus status;
}
