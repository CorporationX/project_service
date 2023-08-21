package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.resource.GetResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.project.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/")
public class ProjectFileController {
    private final ProjectFileService projectFileService;

    @PostMapping("{projectId}/teamMember/{teamMemberId}/files/")
    public ResourceDto uploadFile(@RequestParam("file") MultipartFile multipartFile,
                                  @PathVariable long projectId,
                                  @PathVariable long teamMemberId) {
        return projectFileService.uploadFile(multipartFile, projectId, teamMemberId);
    }

    @GetMapping("teamMember/{teamMemberId}/files/resource/{resourceId}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable long resourceId,
                                                            @PathVariable long teamMemberId) {
        GetResourceDto resourceDto = projectFileService.getFile(resourceId, teamMemberId);
        InputStreamResource inputStreamResource = new InputStreamResource(resourceDto.getInputStream());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resourceDto.getName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resourceDto.getSize())
                .contentType(MediaType.parseMediaType(resourceDto.getType()))
                .body(inputStreamResource);
    }

    @DeleteMapping("teamMember/{teamMemberId}/files/resource/{resourceId}/delete/")
    public void deleteFile(@PathVariable long resourceId, @PathVariable long teamMemberId) {
        projectFileService.deleteFile(resourceId, teamMemberId);
    }
}
