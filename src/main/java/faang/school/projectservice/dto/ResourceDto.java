package faang.school.projectservice.dto;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;

import java.math.BigInteger;

public record ResourceDto(
        Long id,
        long creatorId,
        long updaterId,
        long projectId,
        ResourceType type,
        ResourceStatus status,
        BigInteger size
) {
}
