package faang.school.projectservice.service.filter.sub_project;

import faang.school.projectservice.dto.sub_project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SubProjectFilterService — реализация интерфейса {@link FilterService} для фильтрации подпроектов
 *
 * @author Linempy
 * @since 23.07.2025
 */
@Component
@RequiredArgsConstructor
public class SubProjectFilterServiceImpl implements FilterService<Project, SubProjectFilterDto> {
    private final List<Filter<Project, SubProjectFilterDto>> filters;

    @Override
    public List<Project> getFilteredList(List<Project> entities, SubProjectFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}