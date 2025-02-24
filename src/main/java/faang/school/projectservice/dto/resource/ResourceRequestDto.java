package faang.school.projectservice.dto.resource;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Builder
public record ResourceRequestDto(
        String key,
        BigInteger size,
        String status,
        String type,
        String name
        ) {}
