package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskFilter extends Filter<Task> {

    //TODO: implement filtering
    @Override
    protected boolean applyFilter(Task element, FilterDto filter) {
        return element.getName().matches(filter.getNamePattern())
                && element.getDescription().matches(filter.getDescriptionPattern());
    }

    @Override
    protected boolean isApplicable(FilterDto filter) {
        return true;
    }
}