package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

/**
 * StageFilterDto — Dto для фильтров стадий.
 *
 * @author bozya
 * @since 31.07.2025
 */
public record StageFilterDto(
    List<TeamRole> roles,
    TaskStatus taskStatus
) {}