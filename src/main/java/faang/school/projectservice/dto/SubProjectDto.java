package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class SubProjectDto {

    private Long id;
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;


    private Long parentProjectId;

    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private ProjectStatus status;
    private ProjectVisibility visibility;
    private String coverImageId;
}
