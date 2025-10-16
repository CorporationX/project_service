package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.StageRoles;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * StageDto — Dto для стадий проека.
 *
 * @param stageName название этапа проекта
 * @param projectId ID проекта
 * @param stageRoles необходимые роли для этапа
 * @author bozya
 * @since 31.07.2025
 */
@Builder
public record StageViewDto(
        String stageName,
        Long stageId,
        Long projectId,
        List<StageRoles> stageRoles
        ) {

}