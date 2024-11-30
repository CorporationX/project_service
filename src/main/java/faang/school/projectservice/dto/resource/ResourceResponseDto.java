package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceType;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
public class ResourceResponseDto {

    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private ResourceType type;
    private Long createdById;
    private Long updatedById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
}
