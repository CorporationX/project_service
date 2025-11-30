package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    @Value("${value.file.max_size}")
    private static long MAX_SIZE;

    @PostMapping("/{projectId}/resources")
    public ResourceDto addResource(@PathVariable long projectId, @RequestBody MultipartFile file) throws IOException {
            return resourceService.addCover(projectId, file);
    }
}
