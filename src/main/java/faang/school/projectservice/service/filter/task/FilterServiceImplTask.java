package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FilterServiceImplTask implements FilterService<Task, TaskFilterDto> {
    private final List<Filter<Task, TaskFilterDto>> filters;

    @Override
    public List<Task> getFilteredList(List<Task> entities, TaskFilterDto dto) {
        return applyFilters(filters, entities, dto);
    }
}
