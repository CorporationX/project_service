package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectService {


    void createProject(ProjectDto projectDto);

    void updateStatus(ProjectDto projectDto, ProjectStatus status);

    void updateDescription(ProjectDto projectDto, String description);

    List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto);

    List<ProjectDto> getProjects();

    ProjectDto findById(long id);
}
