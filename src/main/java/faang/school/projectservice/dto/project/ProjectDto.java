package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project name can't be blank")
    private String name;

    @NotBlank(message = "Project description can't be blank")
    private String description;

    @Positive(message = "Storage size can't be negative")
    @Min(value = 1,message = "Storage size can't be less than 1")
    @Max(value = 1000,message = "Storage size can't be greater than 1000")
    private BigInteger storageSize;

    @Positive(message = "Storage max size can't be negative")
    @Min(value = 1,message = "Storage max size can't be less than 1")
    @Max(value = 1000,message = "Storage max size can't be greater than 1000")
    private BigInteger maxStorageSize;

    @NotNull(message = "Project owner id")
    private Long ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ProjectStatus status;

    private ProjectVisibility visibility;
}
