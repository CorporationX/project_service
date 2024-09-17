package faang.school.projectservice.controller;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.service.SubProjectService;
import faang.school.projectservice.util.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import faang.school.projectservice.util.ParentProjectMusNotBeNull;
import faang.school.projectservice.util.RootProjectsParentMustNotBeNull;
import org.springframework.stereotype.Controller;

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
}
