package faang.school.projectservice.contreoller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/project-service/api/v1/")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/projects")
        public ProjectDto create(@RequestBody @Validated ProjectDto projectDto, @RequestHeader(name = "user-id") long id) {
        if (id != projectDto.getOwnerId()) {
            throw new IllegalArgumentException("Access is denied");
        }

        return projectService.create(projectDto);
    }
}