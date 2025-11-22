package faang.school.projectservice.controller;

import faang.school.projectservice.dto.FileDownloadResponse;
import faang.school.projectservice.dto.ResourceDTO;
import faang.school.projectservice.dto.ResourceResponse;
import faang.school.projectservice.enums.Role;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/resources")
@Slf4j
@Validated
public class ResourceController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResourceResponse> uploadFile(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Set<Role> allowedRoles,
            @RequestAttribute("teamMemberId") Long teamMemberId) {

        log.info("Upload request: project={}, file={}, size={}",
                projectId, file.getOriginalFilename(), file.getSize());

        Resource resource = fileStorageService.uploadFile(file, projectId, teamMemberId, allowedRoles);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResourceResponse.from(resource));
    }

    @GetMapping("/{resourceId}/download")
    public ResponseEntity<StreamingResponseBody> downloadFile(
            @PathVariable Long projectId,
            @PathVariable Long resourceId,
            @RequestAttribute("teamMemberId") Long teamMemberId) {
        
        FileDownloadResponse download = fileStorageService.downloadFile(
                resourceId, projectId, teamMemberId);
        
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.getFileName() + "\"");
        
        if (download.getSize() != null && download.getSize() > 0) {
            responseBuilder.contentLength(download.getSize());
        }
        
        StreamingResponseBody stream = outputStream -> {
            try (var inputStream = download.getInputStream()) {
                inputStream.transferTo(outputStream);
            }
        };
        
        return responseBuilder.body(stream);
    }

    @GetMapping("/{resourceId}/url")
    public ResponseEntity<Map<String, Object>> getDownloadUrl(
            @PathVariable Long projectId,
            @PathVariable Long resourceId,
            @RequestAttribute("teamMemberId") Long teamMemberId) {
        String url = fileStorageService.generatePresignedUrl(resourceId, projectId, teamMemberId);

        return ResponseEntity.ok(Map.of("url", url, "expiresIn", 3600));
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long projectId,
            @PathVariable Long resourceId,
            @RequestAttribute("teamMemberId") Long teamMemberId) {

        log.info("Delete request: project={}, resource={}, member={}", projectId, resourceId, teamMemberId);

        fileStorageService.deleteFile(resourceId, projectId, teamMemberId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ResourceDTO>> getProjectFiles(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestAttribute("teamMemberId") Long teamMemberId) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<ResourceDTO> resources = fileStorageService.getProjectFiles(
                projectId, teamMemberId, pageable);

        return ResponseEntity.ok(resources);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ResourceResponse>> uploadMultipleFiles(
            @PathVariable Long projectId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(required = false) Set<Role> allowedRoles,
            @RequestAttribute("teamMemberId") Long teamMemberId) {

        log.info("Bulk upload: project={}, files={}", projectId, files.size());

        if (files.size() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum 10 files can be uploaded at once");
        }

        List<ResourceResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Resource resource = fileStorageService.uploadFile(
                        file, projectId, teamMemberId, allowedRoles);

                responses.add(ResourceResponse.from(resource, "SUCCESS"));

            } catch (Exception e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                responses.add(ResourceResponse.builder()
                        .name(file.getOriginalFilename())
                        .status("FAILED")
                        .error(e.getMessage())
                        .build());
            }
        }
        return ResponseEntity.ok(responses);
    }

}
