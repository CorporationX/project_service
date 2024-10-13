package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.ResourceType;
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