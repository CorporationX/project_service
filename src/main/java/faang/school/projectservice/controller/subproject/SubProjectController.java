package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/subprojects")
@Validated
@RequiredArgsConstructor
public class SubProjectController {
    private final UserContext userContext;
    private final SubProjectService subProjectService;

    @PostMapping
    public SubProjectDto create(@Valid CreateSubProjectDto createSubProjectDto) {
        return subProjectService.create(userContext.getUserId(), createSubProjectDto);
    }

    @PutMapping("/{subProjectId}")
    public SubProjectDto update(@PathVariable long subProjectId, @Valid UpdateSubProjectDto updateSubProjectDto) {
        return subProjectService.update(userContext.getUserId(), subProjectId, updateSubProjectDto);
    }

    @PutMapping("/{subProjectId}/complete")
    public boolean complete(@PathVariable long subProjectId) {
        return subProjectService.complete(userContext.getUserId(), subProjectId);
    }

    @GetMapping("/{subProjectId}")
    public SubProjectDto getById(@PathVariable long subProjectId) {
        return subProjectService.getById(subProjectId);
    }

    @GetMapping("/by-parent-project/{parentProjectId}")
    public List<SubProjectDto> getAllByParentProject(@PathVariable long parentProjectId) {
        return subProjectService.getAllByParentProject(parentProjectId);
    }

    @DeleteMapping("/{subProjectId}")
    public boolean delete(@PathVariable long subprojectId) {
        return subProjectService.delete(userContext.getUserId(), subprojectId);
    }
}
