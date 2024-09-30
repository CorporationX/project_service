package faang.school.projectservice.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.FilterProject;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.SubProjectValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectValidation validation;
    private final ProjectMapper projectMapper;
    private final List<FilterProject<FilterSubProjectDto, QProject>> filters;

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
        return filters.stream()
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