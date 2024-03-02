package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;

    @Operation(summary = "Скачать файл")
    @GetMapping("/{resourceId}")
    public InputStream downloadResourceFile(@PathVariable Long resourceId) {
        return resourceService.downloadResourceFile(resourceId);
    }
    @Operation(
            summary = "Загрузка файла",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @PostMapping( "/{projectId}/file/add")
    public ResourceDto addResourceFile(@PathVariable Long projectId, @RequestBody MultipartFile file) {
        return resourceService.addResourceFile(projectId, file, userContext.getUserId());
    }

    @Operation(
            summary = "Удаление файла",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)}
    )
    @DeleteMapping( "/{resourceId}")
    public void deleteResourceFile(@PathVariable Long resourceId) {
        resourceService.deleteResourceFile(resourceId, userContext.getUserId());
    }
}
