package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/project/subproject")
@RequiredArgsConstructor
public class SubProjectController {
    private final UserContext userContext;
    private final ProjectService projectService;

    @PostMapping(value = "/create")
    ResponseEntity<ProjectDto> createSubProject(@Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createSubProject(userContext.getUserId(),
                createSubProjectDto));
    }

    @PatchMapping(value = "/update")
    ResponseEntity<ProjectDto> updateSubProject(@RequestBody UpdateSubProjectDto updateSubProjectDto) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateSubProject(userContext.getUserId(),
                updateSubProjectDto));
    }

    @GetMapping(value = "/{projectId}")
    ResponseEntity<List<ProjectDto>> getSubProjects(@RequestBody FilterSubProjectDto filter,
                                                    @PathVariable Long projectId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getSubProjects(projectId, filter, size, from));
    }
}