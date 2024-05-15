package faang.school.projectservice.controller;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.ProjectServiceImpl;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectServiceImpl projectService;

    public ProjectDto create(@NonNull ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    public ProjectDto update(@NonNull ProjectDto projectDto) {
        return projectService.update(projectDto);
    }

    public List<ProjectDto> getAll() {
        return projectService.getAll();
    }

    public ProjectDto findById(long id) {
        return projectService.findById(id);
    }

    public List<ProjectDto> getAllByFilter(@NonNull ProjectFilterDto projectFilterDto) {
        return projectService.getAllByFilter(projectFilterDto);
    }
}
