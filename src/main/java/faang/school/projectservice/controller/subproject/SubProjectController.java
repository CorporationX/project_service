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

    @PostMapping("/")
    public SubProjectDto create(@Valid CreateSubProjectDto createSubProjectDto) {
        return subProjectService.create(userContext.getUserId(), createSubProjectDto);
    }

    @PutMapping("/")
    public SubProjectDto update(@Valid UpdateSubProjectDto updateSubProjectDto) {
        return subProjectService.update(userContext.getUserId(), updateSubProjectDto);
    }

    @PutMapping("/{id}/complete")
    public boolean complete(@PathVariable("id") long subprojectId) {
        return subProjectService.complete(userContext.getUserId(), subprojectId);
    }

    @GetMapping("/{id}")
    public SubProjectDto getById(long subprojectId) {
        return subProjectService.getById(subprojectId);
    }

    @GetMapping("/{id}")
    public List<SubProjectDto> getAllByParentProject(long parentProjectId) {
        return subProjectService.getAllByParentProject(parentProjectId);
    }

    @DeleteMapping("/{id}")
    public boolean delete(long subprojectId) {
        return subProjectService.delete(userContext.getUserId(), subprojectId);
    }
}
