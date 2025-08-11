package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.enums.DeleteStrategy;

/**
 * StageDeleteRequestDto — DTO для запроса на удаление этапа.
 *
 * @author bozya
 * @since 06.08.2025
 */
public record StageDeleteRequestDto(
    Long stageId,
    DeleteStrategy deleteStrategy,
    Long moveToStageId
) { }