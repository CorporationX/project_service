package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validation.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    ResponseEntity<ProjectDto> create(@Validated(ValidationGroups.Create.class) @RequestBody ProjectDto projectDto) {
        return new ResponseEntity<>(projectService.create(projectDto), HttpStatus.OK);
    }

    @PutMapping
    ResponseEntity<ProjectDto> update(@Validated(ValidationGroups.Update.class) @RequestBody ProjectDto projectDto) {
        return new ResponseEntity<>(projectService.update(projectDto), HttpStatus.OK);
    }

    @GetMapping("/all")
    ResponseEntity<List<ProjectDto>> getAll() {
        return new ResponseEntity<>(projectService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProjectDto> findById(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/all/filter")
    ResponseEntity<List<ProjectDto>> filteredProjects(@RequestBody(required = false) ProjectFilterDto filterDto) {
        return new ResponseEntity<>(projectService.getAllByFilter(filterDto), HttpStatus.OK);
    }

}
