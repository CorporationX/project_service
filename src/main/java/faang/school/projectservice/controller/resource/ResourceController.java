package faang.school.projectservice.controller.resource;

import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/{projectId}/add-cover-image")
    ResponseEntity<String> addCoveringImage(@RequestParam("file") MultipartFile file, @PathVariable Long projectId) {
        resourceService.addCoveringImageToProject(file, projectId);
        return new ResponseEntity<>("The cover image added successfully", HttpStatus.OK);
    }

}
