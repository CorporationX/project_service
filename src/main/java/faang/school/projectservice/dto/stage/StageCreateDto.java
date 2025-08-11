package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

import static faang.school.projectservice.dto.stage.StageConstraints.MAX_SIZE_STRING;
import static faang.school.projectservice.dto.stage.StageConstraints.MESSAGE_MIN_STAGE_ROLE_COUNT;
import static faang.school.projectservice.dto.stage.StageConstraints.MESSAGE_SIZE_EXECUTORS_INVALID;
import static faang.school.projectservice.dto.stage.StageConstraints.MESSAGE_SIZE_INVALID;
import static faang.school.projectservice.dto.stage.StageConstraints.MIN_SIZE_EXECUTORS;
import static faang.school.projectservice.dto.stage.StageConstraints.MIN_STAGE_ROLE_COUNT;

/**
* StageCreateDto — Dto для создания стадии проекта.
*<p>Пример:</p>
 * <pre>{@code
 * {
 *   "stageName": "Разработка",
 *   "projectId": 1,
 *   "executorIds": [101, 205],
 *   "stageRoles": [
 *     {"role": "DEVELOPER", "count": 2},
 *     {"role": "QA", "count": 1}
 *   ]
 * }
 * }</pre>
* @param stageName Название этапа (не пустое, макс. {MAX_SIZE_STRING} символов)
* @param projectId ID проекта {@link Project}
* @param executorIds Список индентификаторов членов команды (MIN_SIZE_EXECUTORS) {@link TeamMember}
* @param stageRoles Требуемые роли на этапе
* @author bozya
* @since 31.07.2025*/
@Builder
public record StageCreateDto(
        @NotBlank
        @Size(max = MAX_SIZE_STRING, message = MESSAGE_SIZE_INVALID)
        String stageName,
        @NotNull
        Long projectId,
        @Size(max = MIN_SIZE_EXECUTORS, message = MESSAGE_SIZE_EXECUTORS_INVALID)
        List<Long> executorIds,
        @NotNull
        @Size(min = MIN_STAGE_ROLE_COUNT, message = MESSAGE_MIN_STAGE_ROLE_COUNT)
        List<StageRolesDto> stageRoles
) {
}