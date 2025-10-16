package faang.school.projectservice.specification;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Класс содержит спецификации для фильтрации сущностей {@link Stage} с использованием JPA Criteria API.
 * <p>
 * Спецификации позволяют строить динамические SQL-запросы на уровне репозитория.
 * Пример использования:
 * <pre>{@code
 * Specification<Stage> spec = StageSpecification.hasRoles(List.of(TeamRole.MANAGER))
 *     .and(StageSpecification.hasTaskStatus(TaskStatus.IN_PROGRESS));
 * List<Stage> stages = stageRepository.findAll(spec);
 * }</pre>
 * </p>
 *
 * @author bozya
 * @since 06.08.2025
 */
public class StageSpecification {
    public static Specification<Stage> buildSpecification(StageFilterDto filterDto) {
        return Specification.allOf(
                hasRoles(filterDto.roles()),
                hasTaskStatus(filterDto.taskStatus())
        );

    }

    public static Specification<Stage> hasRoles(List<TeamRole> roles) {
        if (roles == null || roles.isEmpty()) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Stage, StageRoles> rolesJoin = root.join("stageRoles");
            return rolesJoin.get("teamRole").in(roles);
        };
    }

    public static Specification<Stage> hasTaskStatus(TaskStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> {

            Join<Stage, Task> tasksJoin = root.join("tasks");
            return cb.equal(tasksJoin.get("status"), status);
        };
    }
}