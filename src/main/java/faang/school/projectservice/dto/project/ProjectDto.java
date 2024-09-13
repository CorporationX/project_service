package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Validated
public class ProjectDto {
    @NotNull
    private Long id;
    @NotBlank(message = "name should not be blank")
    private String name;
    private String description;
    private Long ownerId;
    private Long parentProjectId;
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    @Enumerated(EnumType.STRING)
    private ProjectVisibility visibility;
}
