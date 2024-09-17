package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.subproject.SubProjectService;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ChildrenNotFinishedException;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;

@Controller
public class SubProjectController {
    SubProjectService subProjectService;
    public SubProjectDto createSubProject(Long projectId){
        if (projectId == null){
            throw new NullPointerException();
        }
        try {
            return subProjectService.createSubProject(projectId);
        } catch (RootProjectsParentMustNotBeNull e) {
            throw new RuntimeException(e);
        } catch (CannotCreatePrivateProjectForPublicParent e) {
            throw new RuntimeException(e);
        } catch (ParentProjectMusNotBeNull e) {
            throw new RuntimeException(e);
        }
    }

    public SubProjectDto updateSubProject(SubProjectDto subProjectDto){
        try {
            return subProjectService.updateSubProject(subProjectDto);
        } catch (ChildrenNotFinishedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<SubProjectDto> getAllSubProjectsWithFiltr(SubProjectDto project, String nameFilter, ProjectStatus statusFilter) {
        return subProjectService.getAllSubProjectsWithFiltr(project,nameFilter,statusFilter);
    }
}
