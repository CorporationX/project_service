package faang.school.projectservice.controller.project;

import faang.school.projectservice.service.project.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
public class ProjectFileController {
    private final ProjectFileService projectFileService;

     public ResponseEntity<InputStreamResource> getFile(){

     }
}
