package faang.school.projectservice.controller.resource;

import faang.school.projectservice.client.PostServiceClient;
import faang.school.projectservice.client.own_client.PostDto;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectCoverController {

    private final ProjectService projectService;
    private final PostServiceClient postServiceClient;
    //private final UserContext userContext;

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<ProjectDto> addProjectCover(@PathVariable("projectId") Long projectId,
                                                      @RequestPart("file") MultipartFile file) {
        ProjectDto projectDto = projectService.addCoverImage(projectId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectDto);
    }

    @PutMapping("/{projectId}/cover")
    public ResponseEntity<ProjectDto> updateProjectCover(@PathVariable("projectId") Long projectId,
                                                         @RequestPart("file") MultipartFile file) {
        ProjectDto projectDto = projectService.updateCoverImage(projectId, file);
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/{projectId}/cover/soft")
    public ResponseEntity<ProjectDto> softDeleteProjectCover(@PathVariable("projectId") Long projectId) {
        ProjectDto projectDto = projectService.softDeleteCoverImage(projectId);
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/{projectId}/cover/hard")
    public ResponseEntity<ProjectDto> hardDeleteProjectCover(@PathVariable("projectId") Long projectId) {
        ProjectDto projectDto = projectService.hardDeleteCoverImage(projectId);
        return ResponseEntity.ok(projectDto);
    }

    /******************************************************************************************************************/
    /*@GetMapping("/{id}/get_post")
    public ResponseEntity<PostDto> sendToPost(
            @PathVariable("id") Long postId,
            @RequestHeader(value = "x-user-id", required = false) String userId) {
        try {
            // Заполняем UserContext из заголовка
            if (userId != null) {
                userContext.setUserId(Long.valueOf(userId));
            } else {
                log.warn("x-user-id header is missing, using default or skipping");
                // Можно задать значение по умолчанию, если нужно
                userContext.setUserId(1L);
            }

            var postDtoResponseEntity = postServiceClient.getPost(PostDto.builder().id(postId).build());
            log.info("Received dto: {}", postDtoResponseEntity.getBody());
            return ResponseEntity.ok(postDtoResponseEntity.getBody());
        } catch (Exception e) {
            log.error("Error calling post-service: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/
    @GetMapping("/{id}/get_post")
    public ResponseEntity<PostDto> sendToPost(@PathVariable("id") Long postId) {
        /*var postDtoResponseEntity = postServiceClient.getPost(PostDto.builder().id(postId).build());
        log.info("Received dto: {}", postDtoResponseEntity.getBody());
        return ResponseEntity.ok(postDtoResponseEntity.getBody());*/
        log.info("НАЧАЛО ОТПРАВКИ");
        try {
            var postDtoResponseEntity = postServiceClient.getPost(PostDto.builder().id(postId).build());
            log.info("Received dto: {}", postDtoResponseEntity.getBody());
            return ResponseEntity.ok(postDtoResponseEntity.getBody());
        } catch (Exception e) {
            log.error("Error calling post-service: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
