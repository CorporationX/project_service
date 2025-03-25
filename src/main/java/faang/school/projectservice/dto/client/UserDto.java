package faang.school.projectservice.dto.client;

import lombok.Builder;

@Builder
public record UserDto (
    Long id,
    String username,
    String email
) {
}
