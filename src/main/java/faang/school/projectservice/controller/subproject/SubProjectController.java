package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
    private UserContext userContext;
    private SubProjectService subProjectService;

    @PostMapping("/")
    public SubProjectDto create(long creatorId, @Valid CreateSubProjectDto createSubProjectDto) {
        return subProjectService.create(userContext.getUserId(), createSubProjectDto);
    }

    @PutMapping("/")
    public SubProjectDto update(long requesterId, @Valid UpdateSubProjectDto updateSubProjectDto) {
        return subProjectService.update(requesterId, updateSubProjectDto);
    }

    @PutMapping("/complete/{id}")
    public boolean complete(@PathVariable("id") long subprojectId) {
        return subProjectService.complete(subprojectId);
    }

    public SubProjectDto getById(long subprojectId) {
        return subProjectService.getById(subprojectId);
    }

    public List<SubProjectDto> getAllByParentProject(long parentProjectId) {
        return subProjectService.getAllByParentProject(parentProjectId);
    }

    public boolean delete(long subprojectId) {
        return subProjectService.delete(subprojectId);
    }
}
