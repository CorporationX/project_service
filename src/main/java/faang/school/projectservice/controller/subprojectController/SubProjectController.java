package faang.school.projectservice.controller.subprojectController;

import faang.school.projectservice.dto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.ProjectDto;
import faang.school.projectservice.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class SubProjectController {

    private ProjectService projectService;

    @Autowired
    public SubProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/{parentProjectId}/subprojects")
    public ProjectDto createProject(@PathVariable Long parentProjectId, @RequestBody CreateSubProjectDto createSubProjectDto) {
        return null;
    }
}
