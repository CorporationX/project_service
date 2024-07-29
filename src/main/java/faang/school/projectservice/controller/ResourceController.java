package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    public ResourceDto createResource(@RequestBody MultipartFile multipartFile,
                                      @RequestParam("projectId") Long projectId,
                                      @RequestParam("currentMemberId") Long currentMemberId) {
        return resourceService.addResource(multipartFile, projectId, currentMemberId);
    }

    @DeleteMapping
    public String deleteResource(@RequestParam Long id,@RequestParam Long currentMemberId) {
        resourceService.deleteResource(id,currentMemberId);
        return "Resource deleted successfully";
    }


}
