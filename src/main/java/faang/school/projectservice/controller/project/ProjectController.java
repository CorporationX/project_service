package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/project")
public class ProjectController {
    private final ProjectService projectService;
    private final UserContext userContext;

    @PostMapping("/byFilter")
    public List<ResponseProjectDto> getAllByFilter(@Valid @RequestBody ProjectFilterDto dto) {
        return projectService.getAllByFilter(dto, userContext.getUserId());
    }

    @GetMapping
    public List<ResponseProjectDto> getAll() {
        return projectService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseProjectDto getById(@PathVariable Long id) {
        return projectService.getById(id);
    }
}
