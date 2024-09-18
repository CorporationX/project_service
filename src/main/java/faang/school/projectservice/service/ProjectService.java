package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectService {


    void createProject(ProjectDto projectDto);

    void updateStatus(ProjectDto projectDto, ProjectStatus status);

    void updateDescription(ProjectDto projectDto, String description);

    List<ProjectDto> getProjects();

    ProjectDto findById(long id);

    List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto requester);

    boolean checkUserByPrivateProject(Project project, long requester);
}
