package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;

@FieldNameConstants
@Builder
public record ResourceDto(
        Long id,
        String name,
        String key,
        BigInteger size,
        ResourceType type,
        ResourceStatus status,
        Long projectId
) {
}