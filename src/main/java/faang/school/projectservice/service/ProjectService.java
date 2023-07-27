package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final MomentRepository momentRepository;

    @Transactional
    public List<ProjectDto> getAllProjects() {
        return projectMapper.toDtoList(projectRepository.findAll());
    }

    @Transactional
    public ProjectDto getProject(long projectId) {
        validateProjectExists(projectId);
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
    }

    private void validateProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException("This project doesn't exist");
        }
    }

    public ProjectDto createProject(ProjectDto projectDto) {
        validateOfExistingProjectFromUser(projectDto);
        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        return saveEntityAndReturnDto(project);
    }

    private void validateOfExistingProjectFromUser(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getId(), projectDto.getName())) {
            throw new DataValidationException("The user has already created a project with this name");
        }
    }

    private ProjectDto saveEntityAndReturnDto(Project project) {
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateParentProjectExist(projectDto);
        validateVisibilityConsistency(projectDto);
        validateSubProjectUnique(projectDto);

        Project subProject = projectMapper.toEntity(projectDto);
        Project parentProject = projectRepository.getProjectById(projectDto.getParentId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        Project savedSubProject = projectRepository.save(subProject);
        parentProject.getChildren().add(savedSubProject);
        projectRepository.save(parentProject);

        return projectMapper.toDto(savedSubProject);
    }

    private void validateParentProjectExist(ProjectDto projectDto) {
        if (!projectRepository.existsById(projectDto.getParentId())) {
            throw new DataValidationException("No such parent project");
        }
    }

    private void validateVisibilityConsistency(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentId());

        if (!projectDto.getVisibility().equals(parentProject.getVisibility())) {
            throw new DataValidationException("The visibility of the subproject must be - " +
                    parentProject.getVisibility() + " like the parent project");
        }
    }

    private void validateSubProjectUnique(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentId());
        String subProjectName = projectDto.getName();
        boolean subProjectExists = parentProject.getChildren().stream().anyMatch(
                subProject -> subProject.getName().equals(subProjectName));

        if (subProjectExists) {
            throw new DataValidationException("Subproject with name " + subProjectName + " already exists");
        }
    }

    public ProjectDto updateSubProject(ProjectDto projectDto) {
        validateProjectExists(projectDto.getId());
        validateParentProjectExist(projectDto);

        Project subProject = projectRepository.getProjectById(projectDto.getId());
        ProjectStatus sPStatusDto = projectDto.getStatus();

        if (!sPStatusDto.equals(ProjectStatus.COMPLETED)) {
            subProject.setStatus(sPStatusDto);
        } else if (checkChildrenStatusCompleted(subProject)) {
            createMomentCompletedForSubProject(subProject);
            subProject.setStatus(sPStatusDto);
        } else {
            throw new DataValidationException("Not all subprojects completed");
        }
        return projectMapper.toDto(projectRepository.save(subProject));
    }

    private boolean checkChildrenStatusCompleted(Project project) {
        List<Project> children = project.getChildren();
        if (children == null) {
            return true;
        }
        return children.stream()
                .anyMatch(child -> child.getStatus().equals(ProjectStatus.COMPLETED));
    }

    private void createMomentCompletedForSubProject(Project subProject) {
        Moment moment = new Moment();
        moment.setName(subProject.getName());
        moment.setDescription("All subprojects completed");
        moment.setUserIds(getProjectMembers(subProject));
        momentRepository.save(moment);
    }

    private List<Long> getProjectMembers(Project project) {
        List<Team> teams = Optional.ofNullable(project.getTeams()).orElse(Collections.emptyList());
        return teams.stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream()
                        .map(TeamMember::getUserId))
                .toList();
    }
}
