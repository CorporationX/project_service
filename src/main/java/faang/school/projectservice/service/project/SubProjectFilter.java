package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

public interface SubProjectFilter {
    boolean checkFilters(Project subProject, SubProjectFilterDto filtersDto);
}

@Component
class SubProjectNameFilter implements SubProjectFilter {
    @Override
    public boolean checkFilters(Project subProject, SubProjectFilterDto filtersDto) {
        return filtersDto.getNameFilter() != null && subProject.getName().contains(filtersDto.getNameFilter());
    }
}

@Component
class SubProjectStatusFilter implements SubProjectFilter {
    @Override
    public boolean checkFilters(Project subProject, SubProjectFilterDto filtersDto) {
        return filtersDto.getStatusFilter() != null && filtersDto.getStatusFilter().contains(subProject.getStatus());
    }
}