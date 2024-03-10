package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final UserContext userContext;
    private final ProjectFilterService projectFilterService;
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private final MomentRepository momentRepository;
    private final List<ProjectFilter> filters;

    @Override
    @Transactional
    public ProjectDto createSubProject(CreateSubProjectDto createSubProjectDto) {
        Project parent = projectRepository.getProjectById(createSubProjectDto.getParentId());
        Project subProject = createSubProject(createSubProjectDto, parent);
        return projectMapper.toDto(projectRepository.save(subProject));
    }

    @Transactional
    public ProjectDto updateSubProject(long projectId, UpdateSubProjectDto updateSubProjectDto) {
        Project project = getProject(projectId);
        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED
                && !isEverySubProjectComplete(project)) {
            throw new DataValidationException
                    ("The project cannot be completed while there are open subprojects");
        }

        if (updateSubProjectDto.getVisibility() == ProjectVisibility.PRIVATE
                && !isContainsSubprojects(project)) {
            project.getChildren()
                    .forEach(subProject -> subProject.setVisibility(ProjectVisibility.PRIVATE));
        }
        project.setStatus(updateSubProjectDto.getStatus());
        project.setVisibility(updateSubProjectDto.getVisibility());

        if (updateSubProjectDto.getStatus() == ProjectStatus.COMPLETED) {
            List<Long> userIds = getProjectTeamMemberIds(project);
            createSubprojectsCompletedMoment(project, userIds);
        }
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getFilteredSubProjects(long projectId, ProjectFilterDto projectFilterDto) {
        Project project = getProject(projectId);
        if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException("Object unavailable");
        }
        return filters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto))
                .flatMap(projectFilter -> projectFilter.apply(project.getChildren().stream(), projectFilterDto))
                .distinct()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        setOwner(projectDto);
        validateOwnerIdAndNameExist(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project createdProject = projectJpaRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toDto(createdProject);
    }

    @Override
    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        Project project = getProject(projectDto.getId());
        projectMapper.updateProject(projectDto, project);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAll() {
        List<Project> projects = projectJpaRepository.findAll();
        return projectMapper.toDtos(projects);
    }

    @Override
    public ProjectDto findById(Long id) {
        Project project = getProject(id);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAllByFilter(ProjectFilterDto filterDto) {
        List<Project> projects = projectJpaRepository.findAll();
        List<Project> projectFilteredList = projectFilterService.applyFilters(projects.stream(), filterDto).toList();
        return projectMapper.toDtos(projectFilteredList);
    }

    private Project getProject(Long id) {
        return projectJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Project with id = %d not exist", id)));
    }

    private void setOwner(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
    }

    private void validateOwnerIdAndNameExist(ProjectDto projectDto) {
        if (projectJpaRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(
                    String.format("This user already have a project with name : %s", projectDto.getName()));
        }
    }

    private Project getProject(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    private Project createSubProject(CreateSubProjectDto createSubProjectDto, Project parent) {
        Project entitySubProject = projectMapper.toEntity(createSubProjectDto);
        entitySubProject.setVisibility(parent.getVisibility());
        entitySubProject.setStatus(ProjectStatus.CREATED);
        entitySubProject.setParentProject(parent);
        return entitySubProject;
    }

    private boolean isContainsSubprojects(Project projectToUpdate) {
        return projectToUpdate.getChildren() == null || projectToUpdate.getChildren().isEmpty();
    }

    private boolean isEverySubProjectComplete(Project projectToUpdate) {
        return isContainsSubprojects(projectToUpdate) || projectToUpdate.getChildren().stream()
                .allMatch(subProject -> subProject.getStatus() == ProjectStatus.COMPLETED);
    }

    private List<Long> getProjectTeamMemberIds(Project project) {
        return Optional.ofNullable(project.getTeams())
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .toList();
    }

    private void createSubprojectsCompletedMoment(Project project, List<Long> userIds) {
        Moment moment = momentRepository.save(buildMoment(project, userIds));
        if (project.getMoments() == null) {
            project.setMoments(new ArrayList<>(Arrays.asList(moment)));
        } else {
            project.getMoments().add(moment);
        }
    }

    private Moment buildMoment(Project project, List<Long> userIds) {
        Moment moment = new Moment();
        moment.setName(project.getName());
        moment.setUserIds(userIds);
        return moment;
    }

}