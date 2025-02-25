package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceType;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ResourceReadDto(
        String id,
        String name,
        String key,
        BigInteger size,
        ResourceType type,
        LocalDateTime createdAt,
        Long updatedById,
        Long projectId
) {
}
