package faang.school.projectservice.service;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.util.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import faang.school.projectservice.util.ParentProjectMusNotBeNull;
import faang.school.projectservice.util.RootProjectsParentMustNotBeNull;

import java.util.List;


public interface SubProjectService {

    public SubProjectDto createSubProject(Long projectId) throws RootProjectsParentMustNotBeNull, CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull;

    public SubProjectDto updateSubProject(SubProjectDto subProjectDto) throws ChildrenNotFinishedException;

    public List<SubProjectDto> getAllSubProjectsWithFiltr(SubProjectDto project, String nameFilter, ProjectStatus statusFilter);
}
