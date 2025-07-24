package faang.school.projectservice.service.filter.sub_project;

import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.filter.Filter;

import java.util.stream.Stream;

/**
 * SubProjectNameFilter — фильтрация по имени подпроекта.
 *
 * <p>
 * Реализует фильтрацию потока проектов на основе указанного имени в {@link SubProjectFilterDto}.
 * Реализуется фильтрацию по имени ({@code name}) подпроекта
 * Фильтр применяется только когда в DTO указано имя (не {@code null} и не пустое).
 * </p>
 *
 * @author Linempy
 * @since 23.07.2025
 */
public class SubProjectNameFilter implements Filter<Project, SubProjectFilterDto> {
    @Override
    public boolean isApplicable(SubProjectFilterDto dto) {
        return dto.name() != null && !dto.name().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto dto) {
        if (dto.name() == null) {
            return Stream.empty();
        }
        return projects
                .filter(project -> project.getName().toLowerCase()
                        .contains(dto.name().toLowerCase()));
    }
}