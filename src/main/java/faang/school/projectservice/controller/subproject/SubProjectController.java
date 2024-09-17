package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ChildrenNotFinishedException;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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

    public List<SubProjectDto> getAllSubProjectsWithFiltr(SubProjectDto project, String nameFilter, ProjectStatus statusFilter) {
        return subProjectService.getAllSubProjectsWithFiltr(project, nameFilter, statusFilter);
    }
}
