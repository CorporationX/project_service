package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceResponseDto;
import faang.school.projectservice.service.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/resources")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ResourceController {

    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping
    public ResourceResponseDto uploadNew(@PathVariable @Positive Long projectId,
                                         @RequestParam MultipartFile file) {

        log.info("Received request [POST]ResourceController.uploadNew -- projectId={}, fileName={}, content-type={}",
                projectId, file.getOriginalFilename(), file.getContentType());
        return resourceService.uploadNew(file, projectId, userContext.getUserId());
    }

    @DeleteMapping("/{resourceId}") //Patch/Put? Сама ведь сущность не удаляется
    public ResourceResponseDto delete(@PathVariable @Positive Long projectId,
                                      @PathVariable @Positive Long resourceId) {

        log.info("Received request [DELETE]ResourceController.delete -- projectId={}, resourceId={}",
                projectId, resourceId);
        return resourceService.delete(resourceId, projectId, userContext.getUserId());
    }

    @GetMapping("/{resourceId}")
    public ResourceResponseDto getMetadataById(@PathVariable @Positive Long projectId,
                                       @PathVariable @Positive Long resourceId) {

        log.info("Received request [GET]ResourceController.getMetadataById -- projectId={}, resourceId={}",
                projectId, resourceId);
        return resourceService.getByIdAndProjectId(resourceId, projectId);
    }

    @GetMapping
    public List<ResourceResponseDto> getAllMetadataByProjectId(@PathVariable @Positive Long projectId) {
        log.info("Received request [GET]ResourceController.getAllMetadataByProjectId -- projectId={}",
                projectId);
        return resourceService.getAllByProjectId(projectId);
    }
}
