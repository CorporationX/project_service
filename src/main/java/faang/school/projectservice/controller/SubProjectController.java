package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subproject")
@Slf4j
public class SubProjectController {
    private final ProjectService projectService;

    @PostMapping
    public SubProjectDto createSubProject(@RequestBody @Validated SubProjectDto subProjectDto) {
        log.info("Received request to create a subproject for the Project with ID: {}", subProjectDto.getParentProjectId());
        return projectService.createSubProject(subProjectDto);
    }

    @PutMapping("/update")
    public LocalDateTime updateSubProject(@RequestBody @Validated UpdateSubProjectDto updateSubProjectDto) {
        log.info("Received request to update Subproject with ID: {}", updateSubProjectDto.getId());
        return projectService.updateSubProject(updateSubProjectDto);
    }

    @GetMapping("/{projectId}/children")
    public List<SubProjectDto> getProjectChildrenWithFilter(@RequestBody @Validated ProjectFilterDto filterDto, @PathVariable long projectId) {
        log.info("Received request to retrieve subprojects with filter for Project with ID: {}", projectId);
        return projectService.getProjectChildrenWithFilter(filterDto, projectId);
    }
}