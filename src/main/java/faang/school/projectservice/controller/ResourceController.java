package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final UserContext userContext;
    private final ResourceMapper mapper;

    @PostMapping
    public ResourceDto createResource(@RequestParam("file") MultipartFile multipartFile,
                                      @RequestParam Long projectId) {
        Resource result = resourceService.createResource(
                multipartFile,
                projectId,
                userContext.getUserId());

        return mapper.toDto(result);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
        ResourceDownloadDto dto = resourceService.downloadResource(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(dto.getType());
        headers.setContentDisposition(dto.getContentDisposition());

        return new ResponseEntity<>(dto.getBytes(), headers, HttpStatus.OK);
    }

    @PatchMapping("/{resourceId}")
    public ResourceDto updateResource(@RequestParam("file") MultipartFile multipartFile,
                                      @PathVariable Long resourceId) {
        Resource result = resourceService.updateResource(
                multipartFile,
                resourceId,
                userContext.getUserId());

        return mapper.toDto(result);
    }

    @DeleteMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResource(@PathVariable Long resourceId) {
        resourceService.deleteResource(resourceId, userContext.getUserId());
    }
}
