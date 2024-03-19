package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.CreateSubProjectDto;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.SubProjectService;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectValidator projectValidator;
    private final SubProjectService subProjectService;

    public void createProject(CreateSubProjectDto createSubProjectDto) {
        projectValidator.toCreate(createSubProjectDto);
        subProjectService.createSubProject(createSubProjectDto);
    }

    public void updateProject(ProjectDto projectDto) {
        projectValidator.checkProjectDto(projectDto);
        Moment momentProject = subProjectService.updateProject(projectDto);
    }

    public List<ProjectDto> getAllProjectFilter(ProjectDto projectDto, ProjectFilterDto projectFilterDto) {
        projectValidator.checkProjectDto(projectDto);
        return subProjectService.getAllProjectFilter(projectDto, projectFilterDto);
    }
}