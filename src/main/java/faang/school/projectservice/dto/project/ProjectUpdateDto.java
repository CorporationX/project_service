package faang.school.projectservice.dto.project;


import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
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
public class ProjectUpdateDto {

    @Size(max = 128, message = "Название проекта не может превышать 128 символов")
    private String name;

    @Size(max = 4096, message = "Описание не может превышать 4096 символов")
    private String description;

    private BigInteger maxStorageSize;

    private ProjectStatus status;

    private ProjectVisibility visibility;

    private String coverImageId;
}
