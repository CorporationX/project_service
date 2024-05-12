package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exceptions.DataProjectValidation;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    public ProjectDto create(ProjectDto projectDto) {
        checkIsNull(projectDto);
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataProjectValidation("Проект с таким названием уже существует");
        }
        return projectService.create(projectDto);
    }

    public ProjectDto update(ProjectDto projectDto) {
        checkIsNull(projectDto);
        return projectService.update(projectDto);
    }

    public List<Project> getProjectsByFilter(ProjectFilterDto filterDto) {
        if (filterDto == null) {
            throw new DataProjectValidation("Аргумент filterDto не может быть null");
        }
        return projectService.getProjectsByFilter(filterDto);
    }

    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    public Project getProjectById(Long projectId) {
        if (projectId == null) {
            throw new DataProjectValidation("Аргумент projectId не может быть null");
        }
        return projectService.getProjectById(projectId);
    }

    private void checkIsNull(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataProjectValidation("Аргумент projectDto не может быть null");
        }
    }
}