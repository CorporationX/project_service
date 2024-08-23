package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.SubProjectDtoFilter;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subproject")
@RequiredArgsConstructor
public class SubProjectController {
    private final SubProjectService subProjectService;

    @PostMapping
    public SubProjectDto createSubProject(@Valid @RequestBody CreateSubProjectDto subProjectDto) {
        return subProjectService.createSubProject(subProjectDto);
    }

    @PutMapping
    public SubProjectDto updateSubProject(@RequestBody UpdateSubProjectDto subProjectDto) {
        return subProjectService.updateProject(subProjectDto);
    }

    @GetMapping("/{id}")
    public List<SubProjectDto> getSubProjects(@RequestBody SubProjectDtoFilter filter, @PathVariable Long id) {
        return subProjectService.getProjects(filter, id);
    }
}
