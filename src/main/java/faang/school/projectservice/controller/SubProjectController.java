package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subProject")
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService service;

    @PostMapping("/create")
    public ProjectDto create(@RequestBody CreateSubProjectDto subProjectDto) {
        return service.create(subProjectDto);
    }

    @PostMapping("/update")
    public ProjectDto update(@RequestBody ProjectDto projectDto) {
        return service.update(projectDto);
    }

    @PostMapping("/filter")
    public List<ProjectDto> getSubProjects(@RequestBody SubProjectsFilterDto filterDto) {
        return service.getSubProjects(filterDto);
    }
}
