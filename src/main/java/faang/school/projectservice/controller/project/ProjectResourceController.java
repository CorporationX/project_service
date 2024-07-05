package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ProjectResourceType;
import faang.school.projectservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "projectsResource", description = "Контроллеры для загрузки ресурсов")
@RequestMapping("resources")
public class ProjectResourceController {
    private final ResourceService resourceService;

    @Operation(summary = "Загрузка ресурсов", tags = {"projectsResource"})
    @ApiResponse(responseCode = "200", description = "Ресурс успешно загружен")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PostMapping("{projectId}")
    public ResourceDto uploadResource(
        @PathVariable @NotNull(message = "Project id can't be null")
        @PositiveOrZero(message = "Project id can't be negative")
        Long projectId,
        @RequestParam
        @NotNull(message = "Uploading team member Id Id can't be null")
        @PositiveOrZero(message = "Uploading team member Id can't be negative")
        Long teamMemberId,
        @NotNull
        @RequestPart
        MultipartFile file
    ) {
        return resourceService.uploadResource(projectId, teamMemberId, file, ProjectResourceType.PROJECT_RESOURCE);
    }

    @Operation(summary = "Загрузка обложки проекта", tags = {"projectsResource"})
    @ApiResponse(responseCode = "200", description = "Обложка проекта успешно загружена")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @PostMapping("{projectId}/cover")
    public ResourceDto uploadProjectCover(
        @PathVariable @NotNull(message = "Project id can't be null")
        @PositiveOrZero(message = "Project id can't be negative")
        Long projectId,
        @RequestParam
        @NotNull(message = "Uploading team member Id Id can't be null")
        @PositiveOrZero(message = "Uploading team member Id can't be negative")
        Long teamMemberId,
        @NotNull
        @RequestPart
        MultipartFile file
    ) {
        return resourceService.uploadProjectCover(projectId, teamMemberId, file);
    }

    @Operation(summary = "Удаление обложки проекта", tags = {"projectsResource"})
    @ApiResponse(responseCode = "204", description = "Обложка проекта успешно удалена")
    @ApiResponse(responseCode = "400", description = "Ошибка на стороне клиента")
    @DeleteMapping("{projectId}/cover")
    public void deleteProjectCover(
        @PathVariable @NotNull(message = "Project id can't be null") @PositiveOrZero(message = "Project id can't be negative") Long projectId
    ) {
        resourceService.deleteProjectCover(projectId);
    }
}
