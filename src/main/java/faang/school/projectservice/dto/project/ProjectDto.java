package faang.school.projectservice.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.dto.DtoValidationConstraints;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProjectDto {

    private Long id;
    @NotBlank(message = DtoValidationConstraints.PROJECT_NAME_REQUIRED)
    @Size(min = 3, max = 50, message = DtoValidationConstraints.PROJECT_NAME_CONSTRAINT)
    private String name;
    @NotBlank(message = DtoValidationConstraints.PROJECT_DESCRIPTION_REQUIRED)
    @Size(max = 500, message = DtoValidationConstraints.PROJECT_DESCRIPTION_CONSTRAINT)
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenProjectIds;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private String coverImageId;

}
