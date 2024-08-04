package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/project")
@Tag(name = "Projects", description = "Project handler")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectValidator projectValidator;
    private final ImageService imageService;
    private final S3Service s3Service;

    @Operation(summary = "Create new project")
    @PostMapping("/add")
    public ProjectDto add(@RequestHeader(value = "x-user-id") String userid,
                          @RequestBody ProjectDto projectDto) {
        projectValidator.name(projectDto);
        projectValidator.description(projectDto);
        return projectService.add(projectDto);
    }

    @Operation(summary = "Update project", description = "Update status and/or description")
    @PutMapping
    public ProjectDto update(@RequestBody ProjectDto projectDto) {

        projectValidator.id(projectDto);
        return projectService.update(projectDto);
    }

    @Operation(summary = "Get projects by filter")
    @PostMapping("/getByFilters")
    public List<ProjectDto> getProjectsWithFilters(@RequestHeader(value = "x-user-id") String userid,
                                                   @RequestBody ProjectFilterDto filters) {
        return projectService.getProjectsWithFilters(filters);
    }

    @Operation(summary = "Get all projects")
    @GetMapping("/getAll")
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(summary = "Get projects by id")
    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@RequestHeader(value = "x-user-id") String userid,
                                     @PathVariable Long projectId) {
        projectValidator.id(projectId);
        return projectService.getProjectById(projectId);
    }

    @Operation(summary = "Add cover image to project")
    @PostMapping("/{projectId}/cover")
    public String addCover(@PathVariable Long projectId, @RequestBody MultipartFile coverImage) {
        projectValidator.validateCover(coverImage);
        MultipartFile coverImageCorrect = imageService.resizeImage(coverImage);
        return s3Service.addCover(projectId, coverImageCorrect);
    }
}
