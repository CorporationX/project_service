package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ChildrenNotFinishedException;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;

import java.util.List;


public interface SubProjectService {

    public SubProjectDto createSubProject(Long projectId) throws RootProjectsParentMustNotBeNull, CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull;

    public SubProjectDto updateSubProject(SubProjectDto subProjectDto) throws ChildrenNotFinishedException;

    public List<SubProjectDto> getAllSubProjectsWithFiltr(SubProjectDto project, String nameFilter, ProjectStatus statusFilter);
}
