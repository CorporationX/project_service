package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.service.SubProjectService;
import faang.school.projectservice.util.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import faang.school.projectservice.util.ParentProjectMusNotBeNull;
import faang.school.projectservice.util.RootProjectsParentMustNotBeNull;
import org.springframework.stereotype.Controller;

@Controller
public class SubProjectController {
    SubProjectService subProjectService;
    private ProjectMapper projectMapper;
    public CreateSubProjectDto createSubProject(CreateSubProjectDto subProjectDto){
        if (subProjectDto == null|| subProjectDto.getId() == null){
            throw new NullPointerException();
        }
        try {
            return subProjectService.createSubProject(projectMapper.toEntity(subProjectDto));
        } catch (RootProjectsParentMustNotBeNull e) {
            throw new RuntimeException(e);
        } catch (CannotCreatePrivateProjectForPublicParent e) {
            throw new RuntimeException(e);
        } catch (ParentProjectMusNotBeNull e) {
            throw new RuntimeException(e);
        }
    }

    public CreateSubProjectDto refreshSubProject(CreateSubProjectDto subProjectDto){
        try {
            return subProjectService.refreshSubProject(projectMapper.toEntity(subProjectDto));
        } catch (ChildrenNotFinishedException e) {
            throw new RuntimeException(e);
        }
    }
}
