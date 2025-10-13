package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDto {

    @NotBlank(message = "Название проекта не может быть пустым")
    @Size(max = 128, message = "Название проекта не может превышать 128 символов")
    private String name;

    @Size(max = 4096, message = "Описание не может превышать 4096 символов")
    private String description;

    private BigInteger maxStorageSize;

    @NotNull(message = "Необходимо указать владельца проекта")
    private Long ownerId;

    private Long parentProjectId;

    private ProjectStatus status;

    @NotNull(message = "Необходимо указать видимость проекта")
    private ProjectVisibility visibility;

    private String coverImageId;
}
