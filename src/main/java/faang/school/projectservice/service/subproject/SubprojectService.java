package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.GeneralSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.exception.SubprojectException;
import faang.school.projectservice.mapper.subproject.SubprojectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.messages.SubprojectErrMessage.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubprojectService {
    private final ProjectRepository projectRepository;
    private final SubprojectMapper subprojectMapper;
    private final StageRepository stageRepository;
    private final MomentRepository momentRepository;

    @Transactional
    public GeneralSubprojectDto createSubproject(Long parentProjectId, GeneralSubprojectDto subprojectDto) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);

        Project newSubProject = subprojectMapper.toEntityFromGeneralDto(subprojectDto);
        newSubProject.setParentProject(parentProject);
        newSubProject.setChildren(projectRepository.findAllByIds(subprojectDto.getChildrenIds()));
        newSubProject.setStages(stageRepository.findAllByIds(subprojectDto.getStagesIds()));
        setVisibilitySubproject(newSubProject, subprojectDto.getVisibility());

        parentProject.getChildren().add(newSubProject);
        projectRepository.save(parentProject);

        return subprojectMapper.toGeneralDto(projectRepository.save(newSubProject));
    }

    @Transactional
    public GeneralSubprojectDto updateSubproject(Long subprojectId, SubprojectUpdateDto subprojectDto) {
        Project subprojectForUpdate = projectRepository.getProjectById(subprojectId);
        checkIsSubproject(subprojectForUpdate);

        log.info("Starting updating subproject with id:{}", subprojectId);
        checkPossibilityUpdateSubproject(subprojectForUpdate);

        ProjectStatus statusToUpdate = subprojectDto.getStatus();
        if (nonNull(statusToUpdate)) {
            updateStatus(subprojectForUpdate, statusToUpdate);
            if (isCompletedOrCanceled(statusToUpdate)) {
                updateStatusForChildrenProjects(subprojectForUpdate.getChildren(), statusToUpdate);

                if (statusToUpdate.equals(ProjectStatus.COMPLETED)) {
                    Moment momentAllCompleted = createMomentAllCompleted(subprojectForUpdate);
                    momentRepository.save(momentAllCompleted);
                }
            }
        }

        ProjectVisibility visibilityToUpdate = subprojectDto.getVisibility();
        if (nonNull(visibilityToUpdate)) {
            checkPossibleSetPrivateVisibilitySubproject(subprojectForUpdate, visibilityToUpdate);
            subprojectForUpdate.setVisibility(visibilityToUpdate);
            updateVisibilityForChildren(subprojectForUpdate.getChildren(), visibilityToUpdate);
        }

        log.info("Subproject with id:{} ready to save", subprojectId);
        return subprojectMapper.toGeneralDto(projectRepository.save(subprojectForUpdate));
    }

    private Moment createMomentAllCompleted(Project subproject) {
        List<Team> teams = Optional.ofNullable(subproject.getTeams())
                .orElse(Collections.emptyList());

        List<Long> uniqueUserIds = teams
                .stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream()
                        .map(TeamMember::getUserId))
                .distinct()
                .toList();

        return Moment.builder()
                .name("Выполнены все подпроекты")
                .description("Выполнены все подпроекты")
                .projects(List.of(subproject.getParentProject()))
                .userIds(uniqueUserIds)
                .date(LocalDateTime.now())
                .build();
    }

    private void setVisibilitySubproject(Project subproject, ProjectVisibility visibility) {
        ProjectVisibility visibilityToSet = isNull(visibility)
                ? ProjectVisibility.PUBLIC
                : visibility;

        checkPossibleSetPrivateVisibilitySubproject(subproject, visibilityToSet);

        subproject.setVisibility(visibilityToSet);
    }

    private void checkPossibleSetPrivateVisibilitySubproject(Project subproject, ProjectVisibility visibilityToSet) {
        if (isParentVisibilityPublic(subproject.getParentProject()) &&
                visibilityToSet == ProjectVisibility.PRIVATE) {
            String errorMessage = MessageFormat.format(ERR_VISIBILITY_PARENT_PROJECT_FORMAT, visibilityToSet);
            throw new SubprojectException(errorMessage);
        }
    }

    private void updateVisibilityForChildren(List<Project> childrenSubprojects, ProjectVisibility visibilityToSet) {
        if (nonNull(childrenSubprojects)) {
            for (Project curChild : childrenSubprojects) {
                curChild.setVisibility(visibilityToSet);
                if (nonNull(curChild.getChildren())) {
                    updateVisibilityForChildren(curChild.getChildren(), visibilityToSet);
                }
            }
        }
    }

    private boolean isParentVisibilityPublic(Project parentProject) {
        return parentProject.getVisibility() == ProjectVisibility.PUBLIC;
    }

    private void updateStatusForChildrenProjects(List<Project> childrenSubprojects, ProjectStatus requiredStatus) {
        if (nonNull(childrenSubprojects)) {
            for (Project curChild : childrenSubprojects) {
                curChild.setStatus(requiredStatus);
                if (nonNull(curChild.getChildren())) {
                    updateStatusForChildrenProjects(curChild.getChildren(), requiredStatus);
                }
            }
        }
    }

    private void updateStatus(Project project, ProjectStatus status) {
        project.setStatus(status);
    }

    private boolean isCompletedOrCanceled(ProjectStatus requiredStatus) {
        return requiredStatus == ProjectStatus.CANCELLED || requiredStatus == ProjectStatus.COMPLETED;
    }

    private void checkPossibilityUpdateSubproject(Project subproject) {
        ProjectStatus parentStatus = subproject.getParentProject().getStatus();
        if (parentStatus == ProjectStatus.CANCELLED || parentStatus == ProjectStatus.COMPLETED) {
            String errorMessage = MessageFormat.format(PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT, parentStatus);
            throw new SubprojectException(errorMessage);
        }
    }

    private void checkIsSubproject(Project project) {
        if (isNull(project.getParentProject())) {
            String errorMessage = MessageFormat.format(PROJECT_IS_NOT_SUBPROJECT_FORMAT, project.getId());
            throw new SubprojectException(errorMessage);
        }
    }
}