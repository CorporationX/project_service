package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.sharedfiles.ResourceDto;
import faang.school.projectservice.model.TeamMember;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resources")
public class ResourceController {
    @PostMapping("{projectId}/add")
    public ResourceDto add(@PathVariable Long projectId,
            @RequestBody MultipartFile file,
                                  @RequestParam("teamMember")TeamMember teamMember){
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        String contentType = file.getContentType();

        return new ResourceDto();
    }
}
