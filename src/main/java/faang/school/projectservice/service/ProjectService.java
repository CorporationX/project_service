package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final List<ProjectFilter> projectFilters;
    private final UserContext userContext;

    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        projectValidator.nameExistsAndNotAmpty(projectDto.getName());
        projectValidator.descExistsAndNotEmpty(projectDto.getDescription());

        long projectOwnerId;

        if (Objects.isNull(projectDto.getOwnerId())) {
            projectOwnerId = userContext.getUserId();
        } else {
            projectOwnerId = projectDto.getOwnerId();
        }
        projectValidator.isUniqOwnerAndName(projectOwnerId, projectDto.getName());

        Project project = projectMapper.dtoToProject(projectDto);
        project.setStatus(ProjectStatus.CREATED);

        return projectMapper.projectToDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto update(long projectId, ProjectDto projectDto) {
        projectValidator.isExists(projectId);
        Project project = projectRepository.save(projectMapper.dtoToProject(projectDto));
        return projectMapper.projectToDto(project);
    }

    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) {
        return projectMapper.projectToDto(projectRepository.getProjectById(id));
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getFilteredProject(ProjectFilterDto filters) {
        List<Project> projects = projectRepository.findAll().stream()
                .filter(project -> project.getTeams().stream().anyMatch(this::isUserExistInTeams))
                .toList();

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projects.stream(), filters))
                .map(projectMapper::projectToDto)
                .toList();
    }

    private boolean isUserExistInTeams(Team team) {
        Stream<TeamMember> teamMemberStream = team.getTeamMembers().stream();
        return teamMemberStream.anyMatch(teamMember ->
                teamMember.getUserId() == userContext.getUserId());
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProject() {
        return projectMapper.projectsToDtos(projectRepository.findAll());
    }

    public void delete(long projectId) {
        projectRepository.delete(projectId);
    }
}
