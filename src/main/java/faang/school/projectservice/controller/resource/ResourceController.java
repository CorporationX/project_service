package faang.school.projectservice.controller.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.dto.resource.ResourceUpdateDto;
import faang.school.projectservice.dto.resource.ResourceUploadDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final UserContext userContext;

    @PostMapping
    public ResourceResponseDto upload(@RequestParam("file") MultipartFile file,
                                      @ModelAttribute ResourceUploadDto resourceUploadDto) {
        long projectId = resourceUploadDto.getProjectId();
        long teamMemberId = userContext.getUserId();
        return resourceService.saveResource(file, projectId, teamMemberId);
    }

    @PutMapping("/{resourceId}")
    public ResourceResponseDto reload(@RequestBody ResourceUpdateDto resourceUpdateDto, @PathVariable Long resourceId) {
        long updatedById = userContext.getUserId();
        return resourceService.updateFileInfo(resourceUpdateDto, resourceId, updatedById);
    }

    @DeleteMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long resourceId) {
        long teamMemberId = userContext.getUserId();
        resourceService.deleteFile(resourceId, teamMemberId);
    }

    @GetMapping("/{resourceId}")
    public MultipartFile getResource(@PathVariable Long resourceId) {
        return resourceService.downloadFile(resourceId);
    }
}
