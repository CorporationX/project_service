package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubProjectUpdateDto {
    @NotNull(message = "Project ID can't be null")
    private Long id;
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    @Enumerated(EnumType.STRING)
    private ProjectVisibility visibility;
}
