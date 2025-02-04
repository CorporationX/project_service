package faang.school.projectservice.dto.project.gallery;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class AddImageResponseDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private ResourceType type;
    private ResourceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdByTeamMemberId;
    private Long updatedByTeamMemberId;
    private Long projectId;
}
