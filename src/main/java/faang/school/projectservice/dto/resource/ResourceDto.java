package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ResourceDto(
        Long id,
        String name,
        String key,
        BigInteger size,
        ResourceType type,
        ResourceStatus status,
        LocalDateTime createdAt,
        String createdBy
) {
}
