package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.service.SubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subProject")
@RequiredArgsConstructor
public class SubProjectController {
    private final SubProjectService service;

    @PostMapping("/create")
    public SubProjectDto create(@RequestBody CreateSubProjectDto subProjectDto) {
        return service.createSubProject(subProjectDto);
    }

    @PostMapping("/update")
    public SubProjectDto update(@RequestBody SubProjectDto subProjectDto) {
        return service.updateSubProject(subProjectDto);
    }

    @PostMapping("/filter")
    public List<SubProjectDto> getSubProjects(@RequestBody SubProjectsFilterDto filterDto) {
        return service.getSubProjects(filterDto);
    }
}
