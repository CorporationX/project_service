package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    private Long parentProject;
    private String name;
    private String description;
    private Long ownerId;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private List<Long> children;
    private List<Long> stages;
    private List<Long> tasks;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
