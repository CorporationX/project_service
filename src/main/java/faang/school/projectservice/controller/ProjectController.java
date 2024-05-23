package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/project")
public class ProjectController {
    @PostMapping
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        return null;
    }

    @PutMapping("/{projectId}")
    public ProjectDto update(@PathVariable long projectId, ProjectDto projectDto) {
        return null;
    }

    @GetMapping
    public List<ProjectDto> getFilteredProject() {
        return null;
    }

    @GetMapping("/all")
    public List<ProjectDto> getAllProject() {
        return null;
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProject(@PathVariable long projectId) {
        return null;
    }

    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable long projectId) {

    }
}
