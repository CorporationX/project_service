package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class ProjectServiceImpl implements ProjectService {

    private List<ProjectFilter> filters;
    private ProjectRepository projectRepository;
    private ProjectMapper projectMapper;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        return null;
    }

    @Override
    public ProjectDto update(long userId, ProjectDto projectDto) {
        return null;
    }

    @Override
    public List<ProjectDto> getFilteredProjects(long userId, ProjectDto dto) {
        Stream<Project> projects = projectRepository.findAll().stream();

        return projects
                .filter(project -> {
                    if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
                        return isMemberOfPrivateProject(userId, project);
                    }
                    return true;
                })
                .map(projectMapper::toProjectDto)
                .toList();
    }

    private boolean isMemberOfPrivateProject(long userId, Project project){
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId() == userId);
    }
}
