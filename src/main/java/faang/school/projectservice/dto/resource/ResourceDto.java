package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceType;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private ResourceType type;
    private LocalDateTime createdAt;
    private BigInteger size;
    private LocalDateTime updatedAt;
    private Long projectId;
}