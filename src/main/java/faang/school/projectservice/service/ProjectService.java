package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;

import java.util.List;

public interface ProjectService {

    void createProject(ProjectDto projectDto);

    void updateStatus(long id, ProjectStatus projectStatus);

    void updateDescription(long id, String description);

    List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto requester);

    List<ProjectDto> getProjects();

    boolean checkUserByPrivateProject(Project project, long requester);

    ProjectDto findById(long id);

    ProjectDto createSubProject(long ownerId, CreateSubProjectDto createSubProjectDto);

    ProjectDto updateSubProject(long userId, UpdateSubProjectDto updateSubProjectDto);

    List<ProjectDto> getSubProjects(Long projectId, FilterSubProjectDto filter, Integer size, Integer from);
}