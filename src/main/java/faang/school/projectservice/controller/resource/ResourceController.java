package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resources.interfaces.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping(value = "/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка файла в проект")
    public ResponseEntity<ResourceDto> uploadFile(
            @PathVariable Long projectId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.addResource(projectId, file));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Получение всех файлов проекта")
    public ResponseEntity<List<ResourceDto>> getProjectResources(@PathVariable Long projectId) {
        return ResponseEntity.ok(resourceService.getResources(projectId));
    }

    @DeleteMapping("/{projectId}/{resourceId}")
    @Operation(summary = "Удаление файла из проекта")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long projectId,
            @PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId, projectId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/{resourceId}")
    @Operation(summary = "Обновление файла в проекте")
    public ResponseEntity<ResourceDto> updateFile(
            @PathVariable Long projectId,
            @PathVariable Long resourceId,
            @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(resourceService.updateResource(resourceId, projectId, file));
    }
}
