package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private BigInteger storageSize;
    private BigInteger maxStorageSize;
    private Long ownerId;
    private Long parentProjectId;
    private List<Long> childrenIds;
    private List<Long> taskIds;
    private List<Long> resourceIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
    private String coverImageId;
    private List<Long> teamIds;
    private Long scheduleId;
    private List<Long> stageIds;
    private List<Long> vacancyIds;
    private List<Long> momentIds;
}
