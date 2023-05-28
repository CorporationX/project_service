package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectFilter extends Filter<Project> {

    //TODO: implement filtering
    @Override
    protected boolean applyFilter(Project element, FilterDto filter) {
        return element.getName().matches(filter.getNamePattern())
                && element.getDescription().matches(filter.getDescriptionPattern());
    }

    @Override
    protected boolean isApplicable(FilterDto filter) {
        return true;
    }
}