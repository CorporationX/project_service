package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test")
public class SwaggerTestController {

    @GetMapping("/project/{id}")
    public ProjectDto getProject(@PathVariable Long id) {
        return new ProjectDto();
    }
}
