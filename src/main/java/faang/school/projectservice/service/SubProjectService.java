package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.exception.StatusException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final MomentRepository momentRepository;
    private final List<ProjectFilter> projectFilters;

    @Transactional
    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        validateParentProjectExist(subProjectDto);
        validateVisibilityConsistency(subProjectDto);
        validateSubProjectUnique(subProjectDto);

        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setChildren(projectRepository.findAllByIds(subProjectDto.getChildrenId()));
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        projectRepository.save(subProject);
        parentProject.getChildren().add(subProject);
        projectRepository.save(parentProject);
        return subProjectMapper.toDto(subProject);
    }

    private void validateParentProjectExist(SubProjectDto subProjectDto) {
        if (!projectRepository.existsById(subProjectDto.getParentId())) {
            throw new DataValidException("No such parent project");
        }
    }

    private void validateVisibilityConsistency(SubProjectDto subProjectDto) {
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentId());
        if (!subProjectDto.getVisibility().equals(parentProject.getVisibility())) {
            throw new DataValidException(String.format("The visibility of the subproject must be %s like the parent project", parentProject.getVisibility()));
        }
    }

    private void validateSubProjectUnique(SubProjectDto subProjectDto) {
        Project parentProject = projectRepository.getProjectById(subProjectDto.getParentId());
        String subProjectName = subProjectDto.getName();
        boolean subProjectExists = parentProject.getChildren().stream().anyMatch(
                subProject -> subProject.getName().equals(subProjectName));
        if (subProjectExists) {
            throw new DataValidException(String.format("Subproject with name %s already exists", subProjectName));
        }
    }

    @Transactional
    public SubProjectDto updateSubProject(SubProjectDto subProjectDto) {
        Project subProject = projectRepository.getProjectById(subProjectDto.getId());
        checkStatusesOfChildren(subProject, subProjectDto.getStatus());
        if (subProjectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            closeChildren(subProject);
        }
        setChildrenVisibility(subProject, subProjectDto.getVisibility());

        subProject.setVisibility(subProjectDto.getVisibility());
        subProject.setStatus(subProjectDto.getStatus());
        return subProjectMapper.toDto(subProject);
    }

    private void checkStatusesOfChildren(Project subproject, ProjectStatus status) {
        subproject.getChildren().stream().filter(child -> !child.getStatus().equals(status)).
                forEach(child -> {
                    throw new StatusException("All subprojects of this project should have the same status");
                });
    }

    @Transactional
    private void closeChildren(Project subProject) {
        Moment moment = Moment.builder().name("Выполнены все проекты").build();
        List<Long> userIds = subProject.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(teamMember -> teamMember.getUserId())
                .collect(Collectors.toList());
        moment.setUserIds(userIds);
        momentRepository.save(moment);
    }

    @Transactional
    private void setChildrenVisibility(Project subproject, ProjectVisibility visibility) {
        subproject.getChildren().stream()
                .peek(child -> child.setVisibility(visibility))
                .forEach(child -> projectRepository.save(child));
    }

    public List<SubProjectDto> getSubProjects(ProjectFilterDto filters, long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Project> children = project.getChildren().stream().filter(child -> child.getVisibility() == ProjectVisibility.PUBLIC);
        List<ProjectFilter> projectFilterList = projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();
        for (ProjectFilter filter : projectFilterList) {
            children = filter.apply(children, filters);
        }
        return children.map(subProjectMapper::toDto).toList();
    }
}