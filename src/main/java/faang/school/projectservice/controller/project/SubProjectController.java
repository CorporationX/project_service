package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subProject")
@RequiredArgsConstructor
@Slf4j
@Validated
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ProjectDto create(@RequestBody @Valid CreateSubProjectDto createSubProjectDto) {
        log.info("Запрос на создание подпроекта, у проекта с Id: {}. ", createSubProjectDto.getParentId());
        return projectService.createSubProject(createSubProjectDto);
    }

    @PutMapping("/{projectId}")
    public ProjectDto updateProject(
            @PathVariable @Positive(message = "Id проекта должен быть положительным числом") Long projectId,
            @RequestBody UpdateSubProjectDto updateSubProjectDto) {
        log.info("Запрос на обновление проекта с id: {}.", projectId);
        return projectService.updateProject(projectId, updateSubProjectDto);
    }

    @PostMapping("/search/{projectId}")
    public List<ProjectDto> getFilteredSubProjects(
            @PathVariable @Positive(message = "Id проекта должен быть положительным числом") Long projectId,
            @RequestBody ProjectFilterDto projectFilterDto) {
        log.info("Запрос на поиск подпроекта, у проекта с id: {}.", projectId);
        return projectService.getFilteredSubProjects(projectId, projectFilterDto);
    }
}