package faang.school.projectservice.controller.subprojectController;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.service.SubProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subprojects")
@Validated
@Slf4j
public class SubProjectController {

    private final SubProjectService projectService;

    @Autowired
    public SubProjectController(SubProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/{parentProjectId}")
    public ProjectDto createSubProject(@PathVariable Long parentProjectId, @RequestBody CreateSubProjectDto createSubProjectDto) {
        log.info("Received request to create a subproject for the project with ID: {}", parentProjectId);
        return projectService.createSubProject(parentProjectId, createSubProjectDto);
    }

    @PutMapping("/{subProjectId}")
    public ProjectDto updateSubProject(@PathVariable Long subProjectId, @Valid @RequestBody CreateSubProjectDto createSubProjectDto) {
        log.info("Received request to update subproject with ID: {}", subProjectId);
        return projectService.updateSubProject(subProjectId, createSubProjectDto);
    }

    @GetMapping("/{projectId}/subprojects/{subProjectId}")
    public ProjectDto getSubProject(@PathVariable Long projectId, @PathVariable Long subProjectId) {
        log.info("Received request to get subproject with ID: {} for project {}", subProjectId, projectId);
        return projectService.getSubProject(projectId, subProjectId);
    }
}
