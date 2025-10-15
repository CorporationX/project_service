package faang.school.projectservice.controller.facade.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProjectFacade {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectCreateDto projectCreateDto) {
        Project project = projectService.createProject(projectCreateDto);
        return projectMapper.toProjectDto(project);
    }
}
