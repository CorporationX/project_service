package faang.school.projectservice.service.filter.sub_project;

import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.filter.Filter;

import java.util.stream.Stream;

/**
 * Фильтр подпроектов по статусу.
 * <p>
 * Реализует фильтрацию потока проектов на основе указанного статуса в {@link SubProjectFilterDto}.
 * Фильтр применяется только когда в DTO указан статус (не {@code null}).
 * </p>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * Stream<Project> filtered = statusFilter.apply(projectsStream, filterDto);
 * }</pre>
 *
 * @author Linempy
 * @since 23.07.2025
 */
public class SubProjectStatusFilter implements Filter<Project, SubProjectFilterDto> {
    @Override
    public boolean isApplicable(SubProjectFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto dto) {
        return projects
                .filter(project -> project.getStatus() == dto.status());
    }
}