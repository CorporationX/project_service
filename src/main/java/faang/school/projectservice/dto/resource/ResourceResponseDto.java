package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceResponseDto {

    private long id;

    private String name;

    private BigInteger size;

    private ResourceType type;

    private ResourceStatus status;

    private long projectId;

    private LocalDateTime createdAt;

    private long creatorId;

    private LocalDateTime updatedAt;

    private long updaterId;
}
