package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.Filter;
import faang.school.projectservice.service.filter.NameFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;
    private final ProjectMapper mapper;
    private final List<Filter<Project>> filters;

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        return projectRepository.findAllByIds(ids).stream()
                .map(mapper::toDto)
                .toList();
    }

    //TODO: probably should be refactored
    @Transactional
    public ProjectDto create(ProjectDto projectDto, Long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, projectDto.getName())) {
            throw new DataValidationException(ErrorMessage.PROJECT_ALREADY_EXISTS, projectDto.getName());
        }

        var owner = new TeamMember();
        owner.setRoles(TeamRole.getAll());
        owner.setUserId(userId);
        teamMemberService.create(owner);

        var entity = mapper.toEntity(projectDto);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setStatus(ProjectStatus.CREATED);
        entity.setOwner(owner);
        save(entity);


        var team = new Team();
        team.setProject(entity);
        team.setTeamMembers(new ArrayList<>(List.of(owner)));
        teamService.create(team);

        owner.setTeam(team);
        entity.setTeam(team);

        return mapper.toDto(entity);
    }

    @Transactional
    public ProjectDto update(ProjectDto projectDto, Long userId) {
        Project entity = projectRepository.getProjectById(projectDto.getId());
        if (nonNull(projectDto.getName())
                && !projectRepository.existsByOwnerUserIdAndName(userId, projectDto.getName())) {
            entity.setName(projectDto.getName());
        }
        if (nonNull(projectDto.getDescription())) {
            entity.setDescription(projectDto.getDescription());
        }
        if (nonNull(projectDto.getStatus())) {
            entity.setStatus(projectDto.getStatus());
        }
        entity.setUpdatedAt(LocalDateTime.now());

        return mapper.toDto(save(entity));
    }

    @Transactional
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(long id) {
        return projectRepository.getProjectById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return projectRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByFilter(FilterDto filterDto) {
        List<Project> projects = projectRepository.findAll();

        for (Filter filter : filters) {
            if (filter instanceof NameFilter) {
                projects = filter.applyFilter(projects.stream().map(Project::getName), filterDto);
            }
        }

        return projects.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
