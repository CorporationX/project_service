package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.groups.Groups;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.InvalidFileException;
import faang.school.projectservice.exception.InvalidInvitationStatusException;
import faang.school.projectservice.service.ProjectCoverService;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Validated
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectCoverService projectCoverService;


    // TODO убрать ResponseEntity
    @PostMapping("/{projectId}/cover")
    public ResponseEntity<String> uploadProjectCover(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file) {
        try {
            String coverImageId = projectCoverService.uploadProjectCover(projectId, file);
            return ResponseEntity.ok("Обложка успешно загружена. ID: " + coverImageId);
        } catch (InvalidFileException | EntityNotFoundException | InvalidInvitationStatusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка при загрузке обложки: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
        }
    }

    @GetMapping("/{projectId}")
    public ProjectDto getProjectById(@PathVariable @Positive Long projectId,
                                     @RequestHeader("x-user-id") Long userId) {
        return projectService.findProjectById(projectId, userId);
    }

    @GetMapping
    public List<ProjectDto> getAllProjects(@RequestBody ProjectFilterDto filters,
                                           @RequestHeader("x-user-id") @Positive Long userId) {
        return projectService.findAllProjects(filters, userId);
    }

    @PostMapping
    @Validated({Groups.OnCreate.class})
    public ProjectDto createProject(@RequestBody @Valid ProjectDto projectDto,
                                    @RequestHeader("x-user-id") @Positive Long userId) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userId);
        }
        return projectService.createProject(projectDto);
    }

    @PutMapping
    @Validated({Groups.OnUpdate.class})
    public ProjectDto updateProject(@RequestBody @Valid ProjectDto projectDto) {
        return projectService.updateProject(projectDto);
    }
}
