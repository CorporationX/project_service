package faang.school.projectservice.dto.project;


import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Positive;
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

    @Size(max = 128, message = "Project name cannot exceed 128 characters")
    private String name;

    @Size(max = 4096, message = "Description cannot exceed 4096 characters")
    private String description;

    @Positive(message = "Storage size cannot be negative")
    private BigInteger maxStorageSize;

    private ProjectStatus status;

    private ProjectVisibility visibility;

    @Size(max = 255, message = "Cover image id cannot exceed 255 characters")
    private String coverImageId;
}
