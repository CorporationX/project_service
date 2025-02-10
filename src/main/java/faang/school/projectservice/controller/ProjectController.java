package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectCreateRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpdateRequestDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponseDto save(@RequestBody ProjectCreateRequestDto projectDto) {
        log.info("#ProjectContoller: create request for project:[{}] has been received", projectDto);
        return projectService.save(projectDto);
    }

    @PutMapping("/{id}")
    public ProjectResponseDto update(@PathVariable Long id, @RequestBody ProjectUpdateRequestDto projectDto) {
        log.info("#ProjectContoller: request for updating project:[{}] with id: {} has been received", projectDto, id);
        return projectService.update(id, projectDto);
    }

    @GetMapping("/search")
    public List<ProjectResponseDto> findAllByFilter(ProjectFilterDto filter) {
        log.info("#ProjectController: request to find all projects matching the filter:[{}] has been received", filter);
        return projectService.findAllByFilter(filter);
    }

    @GetMapping
    public List<ProjectResponseDto> findAll() {
        log.info("#ProjectController: request to find all projects has been received");
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponseDto findById(@PathVariable Long id) {
        log.info("#ProjectContoller: request to find a project by its id:{} has been received", id);
        return projectService.findById(id);
    }
}
