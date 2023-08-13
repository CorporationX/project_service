package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ResourceCreateDto;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/upload")
    public ResourceCreateDto createResource(
            @RequestPart("file") MultipartFile file,
            @RequestPart("resourceCreateDto") ResourceCreateDto resourceCreateDto
    ) {
        return resourceService.createResource(resourceCreateDto, file);
    }
}

