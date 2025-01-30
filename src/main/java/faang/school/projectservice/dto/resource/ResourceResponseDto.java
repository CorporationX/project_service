package faang.school.projectservice.dto.resource;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Builder
public record ResourceResponseDto (
        Long id,
        String key,
        BigInteger size,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String status,
        String type,
        String name
) {

}
