package faang.school.projectservice.dto.project;

import faang.school.projectservice.dto.DtoValidationConstraints;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = DtoValidationConstraints.PROJECT_NAME_PATTERN, message = DtoValidationConstraints.PROJECT_NAME_CONSTRAINT)
    private String name;
    @Pattern(regexp = DtoValidationConstraints.PROJECT_DESCRIPTION_PATTERN, message = DtoValidationConstraints.PROJECT_DESCRIPTION_CONSTRAINT)
    private String description;
    @Min(value = 0, message = DtoValidationConstraints.PROJECT_STORAGE_SIZE_LOWER_LIMIT)
    @Max(value = 2048, message = DtoValidationConstraints.PROJECT_STORAGE_SIZE_UPPER_LIMIT)
    private BigInteger storageSize;
    @Min(value = 0, message = DtoValidationConstraints.PROJECT_MAX_STORAGE_SIZE_LOWER_LIMIT)
    @Max(value = 2048, message = DtoValidationConstraints.PROJECT_MAX_STORAGE_SIZE_UPPER_LIMIT)
    private BigInteger maxStorageSize;
    private Long ownerId;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private String coverImageId;
}
