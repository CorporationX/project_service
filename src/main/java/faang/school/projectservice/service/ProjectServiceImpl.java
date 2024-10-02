package faang.school.projectservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.FilterProject;
import faang.school.projectservice.filter.ProjectFilters;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.SubProjectValidation;
import faang.school.projectservice.validator.ValidatorProject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectValidation validation;
    private final ProjectMapper projectMapper;
    private final List<FilterProject<FilterSubProjectDto, QProject>> subProjectFilters;
    private final ProjectMapper mapper;
    private final List<ProjectFilters> filters;
    private final ValidatorProject validator;

    @Override
    public void createProject(ProjectDto projectDto) {
        Project project = mapper.toEntity(projectDto);
        validator.validateProject(projectDto);
        validationDuplicateProjectNames(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        projectRepository.save(project);
    }

    @Override
    public void updateStatus(long projectId, ProjectStatus status) {
        Project project = projectRepository.getProjectById(projectId);

        project.setStatus(status);
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);
    }

    @Override
    public void updateDescription(long projectId, String description) {
        Project project = projectRepository.getProjectById(projectId);

        project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());

        projectRepository.save(project);
    }

    @Override
    public List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto requester) {
        Stream<Project> projectStream = projectRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projectStream,
                        (project, filter) -> filter.apply(project, filterDto),
                        (s1, s2) -> s1)
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PRIVATE)
                        && checkUserByPrivateProject(project, requester.getUserId()))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<ProjectDto> getProjects() {
        return projectRepository.findAll()
                .stream()
                .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC))
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public boolean checkUserByPrivateProject(Project project, long requester) {
        Set<Long> teamMemberIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .collect(Collectors.toSet());

        return teamMemberIds.contains(requester);
    }

    @Override
    public ProjectDto findById(long id) {
        return mapper.toDto(projectRepository.getProjectById(id));
    }

    private List<Project> findByName(String name) {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .filter(project -> project.getName().equals(name))
                .toList();
    }

    private void validationDuplicateProjectNames(ProjectDto projectDto) {
        Project existingProject = findProjectByNameAndOwnerId(projectDto.name(),
                projectRepository.getProjectById(projectDto.id()).getOwnerId());

        if (existingProject != null && existingProject.getId().equals(projectDto.id())) {
            throw new NoSuchElementException("This user already has a project with this name");
        }
    }

    private Project findProjectByNameAndOwnerId(String name, Long ownerId) {
        List<Project> projects = findByName(name);
        for (Project project : projects) {
            if (project.getOwnerId().equals(ownerId)) {
                return project;
            }
        }
        return null;
    }

    /////////////////////////////////////////////////////////////////////
    @Transactional
    @Override
    public ProjectDto createSubProject(long ownerId, CreateSubProjectDto createSubProjectDto) {
        Project parent = projectRepository.getProjectById(createSubProjectDto.parentId());
        Project subProject = projectRepository.save(projectMapper.toEntity(createSubProjectDto, parent, ownerId));
        return projectMapper.toDto(subProject);
    }

    @Transactional
    @Override
    public ProjectDto updateSubProject(long userId, UpdateSubProjectDto updateSubProjectDto) {
        Project project = projectRepository.getProjectById(updateSubProjectDto.projectId());
        validation.updateSubProject(userId, updateSubProjectDto, project);
        if (updateSubProjectDto.status() != null) {
            project.setStatus(updateSubProjectDto.status());
            if (project.getStatus().equals(ProjectStatus.COMPLETED)) {
                project.getMoments().add(getMomentAllSubprojectsCompleted(project));
            }
        }
        if (updateSubProjectDto.visibility() != null &&
                updateSubProjectDto.visibility().equals(ProjectVisibility.PRIVATE)) {
            setVisibilityPrivateInSubprojects(project.getChildren());
        }
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    @Override
    public List<ProjectDto> getSubProjects(Long projectId, FilterSubProjectDto filter, Integer size, Integer from) {
        BooleanExpression finalCondition = getCondition(filter);
        Sort sort = Sort.by("id").ascending();
        PageRequest pageRequest = PageRequest.of(from / size, size, sort);
        Page<Project> projects;
        if (finalCondition == null) {
            projects = projectRepository.findAll(pageRequest);
        } else {
            projects = projectRepository.findAll(finalCondition, pageRequest);
        }
        return projectMapper.toDtos(projects.stream().toList());
    }

    private BooleanExpression getCondition(FilterSubProjectDto filterSubProjectDto) {
        QProject qProject = QProject.project;
        return subProjectFilters.stream()
                .map(filter -> filter.getCondition(filterSubProjectDto, qProject))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(qProject.visibility.eq(ProjectVisibility.PUBLIC), BooleanExpression::and);
    }

    private void setVisibilityPrivateInSubprojects(List<Project> children) {
        if (children == null || children.isEmpty()) {
            return;
        }
        children.forEach(subProject -> {
            subProject.setVisibility(ProjectVisibility.PRIVATE);
            setVisibilityPrivateInSubprojects(subProject.getChildren());
        });
    }

    private Moment getMomentAllSubprojectsCompleted(Project project) {
        Moment moment = new Moment();
        moment.setName("All subprojects completed");
        moment.setUserIds(
                project.getTeams()
                        .stream()
                        .flatMap((team -> team.getTeamMembers().stream()
                                .map(TeamMember::getId)))
                        .toList());
        return moment;
    }
}