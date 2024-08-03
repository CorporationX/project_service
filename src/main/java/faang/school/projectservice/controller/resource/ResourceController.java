package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    @PostMapping("/project/{projectId}/add")
    public ResourceDto add(@PathVariable Long projectId,
            @RequestHeader(value = "x-user-id") String userid,
            @RequestParam(value = "allowedTeamRoles", required = false) List<TeamRole> allowedTeamRoles,
            @RequestBody MultipartFile file){
//        String fileName = file.getOriginalFilename();
//        long fileSize = file.getSize();
//        String contentType = file.getContentType();
//        TeamMember teamMember = new TeamMember();

        return resourceService.add(file,allowedTeamRoles,projectId);
    }

    @GetMapping("/project/{projectId}/get/{resourceId}")
    public ResponseEntity<InputStreamResource> get(@PathVariable Long resourceId){


        return resourceService.get(resourceId);
    }

//    @PostMapping("{projectId}/add")
//    public ResourceDto add(@PathVariable Long projectId,
//                           @RequestBody MultipartFile file,
//                           @RequestParam("teamMember")TeamMember teamMember){
//        String fileName = file.getOriginalFilename();
//        long fileSize = file.getSize();
//        String contentType = file.getContentType();
//        resourceService.add(file,teamMember,projectId);
//        return new ResourceDto();
//    }
}
