package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import faang.school.projectservice.util.ParentProjectMusNotBeNull;
import faang.school.projectservice.util.RootProjectsParentMustNotBeNull;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SubProjectService {

    public CreateSubProjectDto createSubProject(Project subProjectDto) throws RootProjectsParentMustNotBeNull, CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull;

    public CreateSubProjectDto refreshSubProject(Project subProjectDto) throws ChildrenNotFinishedException;

    public List<CreateSubProjectDto> getAllSubProjectsWithFiltr(CreateSubProjectDto project, String nameFilter, ProjectStatus statusFilter);
}
