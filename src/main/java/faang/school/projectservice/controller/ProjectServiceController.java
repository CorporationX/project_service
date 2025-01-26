package faang.school.projectservice.controller;


import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectServiceController {


    @GetMapping()
    public String getAllProjects() {
        return "all-projects";
    }

    @PostMapping("/{id}")
    public String getProject(@PathVariable("id") @NotNull Long id) {
        return String.format("Get project id: {}", id);
    }

    @PutMapping("/{id}")
    public String updatePoject(@PathVariable("id") @NotNull Long id) {
        return String.format("Update project id: {}", id);
    }
}
