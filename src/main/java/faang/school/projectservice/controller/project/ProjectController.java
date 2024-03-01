package faang.school.projectservice.controller.project;

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

    @PostMapping("/create")
    ResponseEntity<ProjectDto> createProject(@Validated(ValidationGroups.Create.class) @RequestBody ProjectDto projectDto) {
        ProjectDto responceProjectDto = projectService.create(projectDto);
        return new ResponseEntity<>(responceProjectDto, HttpStatus.OK);
    }

    @PutMapping("/update")
    ResponseEntity<ProjectDto> updateProject(@Validated(ValidationGroups.Update.class) @RequestBody ProjectDto projectDto) {
        ProjectDto responceProjectDto = projectService.update(projectDto);
        return new ResponseEntity<>(responceProjectDto, HttpStatus.OK);
    }

    @GetMapping("/all")
    ResponseEntity<List<ProjectDto>> getAll() {
        List<ProjectDto> projectsDtos = projectService.getAll();
        return new ResponseEntity<>(projectsDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ProjectDto> findById(@PathVariable Long id) {
        ProjectDto projectDto = projectService.findById(id);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @GetMapping("/all/filter")
    ResponseEntity<List<ProjectDto>> getAllByFilter(@RequestBody(required = false) ProjectFilterDto filterDto) {
        List<ProjectDto> allByFilter = projectService.getAllByFilter(filterDto);
        return new ResponseEntity<>(allByFilter, HttpStatus.OK);
    }

}
