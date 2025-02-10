package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubProjectCreateDto {
    @NotBlank(message = "Название подпроекта не может быть пустым")
    private String name;
    @NotNull
    private Long ownerId;
    @NotNull
    private Long parentProjectId;
    @NotNull
    private ProjectVisibility visibility;
}
