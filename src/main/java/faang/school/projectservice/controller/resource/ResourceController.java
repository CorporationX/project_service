package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService service;

    @GetMapping("/{id}")
    public String getResourceUrl(@PathVariable long id) {
        return service.getResource(id);
    }

    @PostMapping
    public ResourceDto createResource(@RequestHeader("x-user-id") long userId, @RequestParam("projectId") long projectId, @RequestParam("file") @NotNull MultipartFile file) {
        return service.createResource(userId, projectId, file);
    }

    @PatchMapping("/{id}")
    public ResourceDto updateResource(@RequestHeader("x-user-id") long userId, @PathVariable long id, @RequestParam("file") @NotNull MultipartFile file) {
        return service.updateResource(userId, id, file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@RequestHeader("x-user-id") long userId, @PathVariable long id) {
        service.deleteResource(userId, id);
        return ResponseEntity.noContent().build();
    }
}
