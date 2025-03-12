package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("subProject")
@RequiredArgsConstructor
public class SubProjectController {
    private final ProjectService service;

    @PostMapping("/create")
    public CreateSubProjectDto create(@RequestBody CreateSubProjectDto subProjectDto) {
        return service.create(subProjectDto);
    }
}
