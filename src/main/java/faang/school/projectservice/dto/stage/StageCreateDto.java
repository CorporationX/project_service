package faang.school.projectservice.dto.stage;

import lombok.Builder;

import java.util.List;

@Builder
public record StageCreateDto(
        String stageName,
        Long projectId,
        List<StageRoleDto> roles) {
}