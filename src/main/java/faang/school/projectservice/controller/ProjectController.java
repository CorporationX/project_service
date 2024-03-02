package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.dto.ProjectUpdateDto;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.validator.ProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectValidator projectValidator;
    private final ResourceService resourceService;
    private final UserContext userContext;


    @Operation(parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}, summary = "Create new project")
    //^ для возможности вручную добавлять id пользователя в сваггере и добавления названия эндпоинтов там же
    @PostMapping // не идемпотентный, всегда создает новую сущность
    public ProjectDto createProject(@RequestBody ProjectDto projectDto) {
        projectValidator.validateCreateProject(projectDto);
        return projectService.createProject(projectDto);
    }

    @Operation(parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}, summary = "update project")
    @PutMapping("/{id}")
    // идемпотентный andPoint - сколько бы раз не вызывали с одинаковыми параметрами-результат один и тот же
    public ProjectDto updateProject(@PathVariable Long id, @RequestBody ProjectUpdateDto projectDto) {
        projectValidator.validateUpdateProject(projectDto);
        return projectService.updateProject(id, projectDto);
    }

    @Operation(parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}, summary = "Get all projects with filters")
    @PutMapping("/filter")
    public List<ProjectDto> getAllProjectsWithFilter(@RequestBody ProjectFilterDto projectFilterDto) {
        projectValidator.validateFilter(projectFilterDto);
        return projectService.getAllProjectsWithFilter(projectFilterDto);
    }

    @Operation(parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}, summary = "Get all projects")
    @GetMapping
    public List<ProjectDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}, summary = "Get project by id")
    @GetMapping("/{id}") // всегда идемпотентный andPoint
    public ProjectDto getProjectById(@PathVariable Long id) {
        projectValidator.validateProjectId(id);
        return projectService.getProjectById(id);
    }

    @PutMapping("/{projectId}/cover")
    public ResourceDto addCover(@PathVariable long projectId, @RequestPart MultipartFile file) {
        return resourceService.addCoverToProject(projectId, userContext.getUserId(), file);
    }

    @GetMapping("/cover/{projectId}")
    public ResponseEntity<byte[]> getCover(@PathVariable long projectId) {
        byte[] imageBytes = null;
        try {
            imageBytes = resourceService.downloadCoverByProjectId(projectId).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageBytes, httpHeaders, HttpStatus.OK);
    }
}

