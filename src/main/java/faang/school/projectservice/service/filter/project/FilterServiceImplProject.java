package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FilterServiceImplProject implements FilterService<Project, ProjectFilterDto> {
    private final List<Filter<Project, ProjectFilterDto>> filters;

    @Override
    public List<Project> getFilteredList(List<Project> entities, ProjectFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}
