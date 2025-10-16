package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

/**
 * StageUpdateDto — Dto для обновления информации об этапе.
 * <p>
 *
 * </p>
 *
 * @param stageName имя этапа
 * @param stageRoles список ролей на этапе
 * @param teamMemberIds Id участников команды
 * @author bozya
 * @since 31.07.2025
 */
@Builder
public record StageUpdateDto(
        @NotBlank
        String stageName,
        List<StageRolesDto> stageRoles,
        List<Long> teamMemberIds
) { }