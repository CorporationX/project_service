package faang.school.projectservice.service.filter.intership;

import faang.school.projectservice.apimodel.InternshipFilterDto;
import faang.school.projectservice.apimodel.InternshipStatusDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Internship_;
import faang.school.projectservice.model.TeamRole;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * Класс {@code InternshipSpecifications} содержит статические методы для построения JPA {@link Specification}
 * для сущности {@link Internship} на основе различных критериев фильтрации.
 * <p>
 * Основная задача класса — формировать спецификации для динамической фильтрации сущностей Internship,
 * учитывая, что некоторые поля фильтра могут быть опциональными (например, список статусов или ролей).
 * </p>
 * <p>
 * Спецификации строятся с использованием сгенерированного класса метамодели {@link Internship_},
 * что обеспечивает типобезопасный доступ к полям сущности.
 * </p>
 *
 * @author agent
 * @since 10.08.2025
 */
public class InternshipSpecifications {

    /**
     * Создаёт комплексную спецификацию для фильтрации сущностей {@link Internship} по параметрам из {@link InternshipFilterDto}.
     * <p>
     * Если в фильтре некоторые поля не заданы (null или пустые), соответствующая фильтрация по ним не применяется.
     * </p>
     *
     * @param dto объект с параметрами фильтрации {@link InternshipFilterDto}
     * @return спецификация {@link Specification} для фильтрации сущностей Internship
     */
    public static Specification<Internship> byFilter(InternshipFilterDto dto) {
        return Specification
                .where(hasProjectId(dto.getProjectId()))
                .and(hasStatuses(dto.getStatuses()))
                .and(hasRoles(dto.getRoles()));
    }

    /**
     * Создаёт спецификацию для фильтрации по идентификатору проекта.
     * <p>
     * Если параметр {@code projectId} равен {@code null}, возвращается спецификация, которая не накладывает ограничений (conjunction).
     * </p>
     *
     * @param projectId идентификатор проекта для фильтрации
     * @return спецификация {@link Specification} по проекту
     */
    public static Specification<Internship> hasProjectId(Long projectId) {
        return (root, query, cb) -> {
            if (projectId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get(Internship_.project).get("id"), projectId);
        };
    }

    /**
     * Создаёт спецификацию для фильтрации по списку статусов {@link InternshipStatus}.
     * <p>
     * Если список {@code statuses} равен {@code null} или пуст, возвращается спецификация без ограничений (conjunction).
     * </p>
     *
     * @param statuses список статусов для фильтрации
     * @return спецификация {@link Specification} по статусам
     */
    public static Specification<Internship> hasStatuses(List<InternshipStatusDto> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        List<InternshipStatus> modelStatuses = statuses.stream()
                .map(status -> InternshipStatus.valueOf(status.name()))
                .toList();
        return (root, query, cb) -> root.get(Internship_.status).in(modelStatuses);
    }

    /**
     * Создаёт спецификацию для фильтрации по списку ролей {@link TeamRole}.
     * <p>
     * Если список {@code roles} равен {@code null} или пуст, возвращается спецификация без ограничений (conjunction).
     * </p>
     *
     * @param roles список ролей для фильтрации
     * @return спецификация {@link Specification} по ролям
     */
    public static Specification<Internship> hasRoles(List<faang.school.projectservice.apimodel.TeamRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        List<TeamRole> modelRoles = roles.stream()
                .map(role -> TeamRole.valueOf(role.name()))
                .toList();
        return (root, query, cb) -> root.get(Internship_.role).in(modelRoles);
    }
}