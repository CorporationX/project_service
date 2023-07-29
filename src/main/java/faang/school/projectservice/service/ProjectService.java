package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final StageRepository stageRepository;
    private final MomentRepository momentRepository;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateSubProject(projectDto);
        validateParentProjectExist(projectDto);
        checkSubProjectNotPrivateOnPublicProject(projectDto);
        validateProjectNotExist(projectDto);
        Project subProject = subProjectMapper.toEntity(projectDto);
        subProject.setChildren(projectRepository.findAllByIds(projectDto.getChildrenIds()));
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        List<Stage> stages = projectDto.getStagesId().stream()
                .map(stageRepository::getById)
                .toList();
        subProject.setStages(stages);
        parentProject.getChildren().add(subProject);
        projectRepository.save(subProject);
        projectRepository.save(parentProject);
        return subProjectMapper.toDto(subProject);
    }

    public List<ProjectDto> createSubProjects(List<ProjectDto> projectsDtos) {
        projectsDtos.forEach(this::validateSubProject);
        return projectsDtos.stream()
                .map(this::createSubProject)
                .toList();
    }

    public Timestamp updateSubProject(ProjectDto projectDto) {
        Project updatedProject = subProjectMapper.toEntity(projectDto);
        Project originalProject = projectRepository.getProjectById(projectDto.getId());

        if (projectDto.getStatus() != null && projectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            List<Project> allProjects = collectAllProjectsByUniqueId(originalProject, projectDto);
            allProjects.forEach(this::checkSubProjectStatusCompleteOrCancelled);
            updatedProject.setChildren(allProjects);
            createAndSaveMoment(projectDto);
            updateAllNeededFields(projectDto, originalProject, updatedProject);
            projectRepository.save(updatedProject);
            return null;//TimeStamp
        }

        updateAllNeededFields(projectDto, originalProject, updatedProject);

        if (projectDto.getVisibility() != null && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            List<Project> subProjects = collectAllProjectsByUniqueId(originalProject, projectDto);
            subProjects.stream()
                    .peek(subProject -> subProject.setVisibility(ProjectVisibility.PRIVATE))
                    .forEach(projectRepository::save);
            updatedProject.setChildren(subProjects);
        }
        projectRepository.save(updatedProject);
        return null;//timestamp
    }

    public void createAndSaveMoment(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());
        Moment moment = new Moment();
        moment.setName(String.format("%s project tasks", projectDto.getName()));
        moment.setDescription(String.format("All tasks are completed in %s project", projectDto.getName()));
        moment.setResource(project.getResources());
        moment.setProjects(project.getChildren());
        moment.getProjects().add(project);
        moment.setUserIds(collectAllUsersIdOnProject(project));
        moment.setImageId(project.getCoverImageId());
        moment.setCreatedBy(projectDto.getOwnerId());
        moment.setUpdatedBy(project.getOwnerId());
        momentRepository.save(moment);
    }

    public List<Long> collectAllUsersIdOnProject(Project project) {
        Set<Long> userIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()
                        .map(TeamMember::getId))
                .collect(Collectors.toSet());
        project.getChildren().stream()
                .flatMap(subProject -> momentRepository.findAllByProjectId(subProject.getId()).stream())
                .flatMap(subProjectMoment -> subProjectMoment.getUserIds().stream())
                .forEach(userIds::add);
        return userIds.stream().toList();
    }

    public void updateAllNeededFields(ProjectDto projectDto, Project originalProject, Project updatedProject) {
        setThreeField(projectDto, updatedProject);
        Project originalWithNewParrentProject = changeParentProject(projectDto, originalProject);
        updatedProject.setParentProject(originalWithNewParrentProject.getParentProject());
        updatedProject.setChildren(collectAllProjectsByUniqueId(originalProject, projectDto));
    }

    public List<Project> collectAllProjectsByUniqueId(Project originalProject, ProjectDto projectDto) {
        List<Project> allSubProjects = new ArrayList<>();
        if (!originalProject.getChildren().isEmpty()) {
            if (projectDto.getChildrenIds() != null && !projectDto.getChildrenIds().isEmpty()) {
                List<Long> dataBaseProjectIds = originalProject.getChildren().stream()
                        .map(Project::getId).toList();
                List<Long> allSubProjectsIds = projectDto.getChildrenIds().stream()
                        .flatMap(subProjectId -> dataBaseProjectIds.stream()
                                .filter(id -> !Objects.equals(id, subProjectId)))
                        .toList();
                allSubProjects.addAll(projectRepository.findAllByIds(allSubProjectsIds));
            } else {
                allSubProjects.addAll(originalProject.getChildren());
            }
        } else if (projectDto.getChildrenIds() != null && !projectDto.getChildrenIds().isEmpty()) {
            allSubProjects.addAll(projectRepository.findAllByIds(projectDto.getChildrenIds()));
        }
        return allSubProjects;
    }

    private void setThreeField(ProjectDto projectDto, Project project) {
        if (projectDto.getStatus() != null) {
            project.setStatus(projectDto.getStatus());
        }
        if (projectDto.getStagesId() != null) {
            List<Stage> stages = projectDto.getStagesId().stream()
                    .map(stageRepository::getById)
                    .toList();
            project.setStages(stages);
        }
        if (projectDto.getVisibility() != null) {
            checkSubProjectNotPrivateOnPublicProject(projectDto);
            project.setVisibility(projectDto.getVisibility());
        }
    }

    private Project changeParentProject(ProjectDto projectDto, Project project) {
        if (projectDto.getParentProjectId() != null && !Objects.equals(project.getParentProject().getId(), projectDto.getParentProjectId())) {
            Project oldParentProject = project.getParentProject();
            oldParentProject.getChildren().remove(project);
            projectRepository.save(oldParentProject);
            Project newParentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
            newParentProject.getChildren().add(project);
            projectRepository.save(newParentProject);
            project.setParentProject(newParentProject);
        }
        return project;
    }

    private void validateSubProject(ProjectDto projectDto) {
        if (projectDto.getOwnerId() <= 0) {
            throw new DataValidationException("Owner id cant be less then 1");
        }
        if (projectDto.getChildrenIds() == null) {
            throw new DataValidationException("Subprojects can be empty but not null");
        }
        if (projectDto.getStatus() == null) {
            throw new DataValidationException("Project status cant be null");
        }
        if (projectDto.getVisibility() == null) {
            throw new DataValidationException(String.format("Visibility of subProject '%s' must be specified as 'private' or 'public'.", projectDto.getName()));
        }
        if (projectDto.getParentProjectId() <= 0) {
            throw new DataValidationException("ParentProjectId cant be less 0 or 0");
        }
    }

    private void checkSubProjectNotPrivateOnPublicProject(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject.getVisibility().equals(ProjectVisibility.PUBLIC) && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException(String.format("Private SubProject; %s, cant be with a public Parent Project: %s", projectDto.getName(), parentProject.getName()));
        }
    }

    private void validateParentProjectExist(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
        if (parentProject == null) {
            throw new EntityNotFoundException(String.format("Parent project not found by id: %s", projectDto.getParentProjectId()));
        }
    }

    private void validateProjectNotExist(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException(String.format("Project %s is already exist", projectDto.getName()));
        }
    }

    private void checkSubProjectStatusCompleteOrCancelled(Project subProject) {
        if (subProject.getStatus() != ProjectStatus.COMPLETED && subProject.getStatus() != ProjectStatus.CANCELLED) {
            throw new DataValidationException("Can't close project if subProject status are not complete or cancelled");
        }
    }
}