package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.ProjectFilterDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.exception.ChildrenNotFinishedException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SubProjectController {
    private final SubProjectService subProjectService;

    public SubProjectDto createSubProject(@NotNull Long projectId) {
        return subProjectService.createSubProject(projectId);
    }

    public SubProjectDto updateSubProject(SubProjectDto subProjectDto) {
        try {
            return subProjectService.updateSubProject(subProjectDto);
        } catch (ChildrenNotFinishedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SubProjectDto> getAllSubProjectsWithFiltr(Long projectId, ProjectFilterDto filtrDto) {
        return subProjectService.getAllSubProjectsWithFiltr(projectId,filtrDto);
    }
}
