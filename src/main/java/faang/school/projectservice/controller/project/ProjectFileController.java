package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.resource.GetResourceDto;
import faang.school.projectservice.service.project.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
public class ProjectFileController {
    private final ProjectFileService projectFileService;

//     public ResponseEntity<Resource> getFile(long resourceId,long teamMemberId){
//         GetResourceDto resourceDto = projectFileService.getFile(resourceId, teamMemberId);
//
//     }
}
