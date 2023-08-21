package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoReqCreate;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.exception.SubprojectException;
import faang.school.projectservice.mapper.subproject.SubprojectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import static faang.school.projectservice.commonMessage.SubprojectErrMessage.PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT;
import static faang.school.projectservice.commonMessage.SubprojectErrMessage.PROJECT_IS_NOT_SUBPROJECT_FORMAT;
import static faang.school.projectservice.messages.SubprojectErrMessage.ERR_VISIBILITY_PARENT_PROJECT_FORMAT;

@Service
@RequiredArgsConstructor
public class SubprojectService {
    private final ProjectRepository projectRepository;
    private final SubprojectMapper subprojectMapper;
    private final StageRepository stageRepository;
    private final MomentRepository momentRepository;

    @Transactional
    public SubprojectDtoReqCreate createSubproject(Long parentProjectId, SubprojectDtoReqCreate subprojectDto) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);

        Project newSubProject = subprojectMapper.toEntityFromDtoCreate(subprojectDto);
        newSubProject.setParentProject(parentProject);
        newSubProject.setChildren(projectRepository.findAllByIds(subprojectDto.getChildrenIds()));
        newSubProject.setStages(stageRepository.findAllByIds(subprojectDto.getStagesIds()));
        setVisibilitySubproject(newSubProject, subprojectDto.getVisibility());

        parentProject.getChildren().add(newSubProject);
        projectRepository.save(parentProject);

        return subprojectMapper.toDtoReqCreate(projectRepository.save(newSubProject));
    }

    @Transactional
    public SubprojectDtoReqCreate updateSubproject(Long subprojectId, SubprojectUpdateDto subprojectDto) {
        Project subprojectForUpdate = projectRepository.getProjectById(subprojectId);
        checkIsSubproject(subprojectForUpdate);
        checkPossibilityUpdateSubproject(subprojectForUpdate);

        ProjectStatus statusToUpdate = subprojectDto.getStatus();
        if (Objects.nonNull(statusToUpdate)) {
            updateStatus(subprojectForUpdate, statusToUpdate);
        }

        if (isStatusCompletedForAllChildren(subprojectForUpdate)) {
            createMomentWhenSubprojectCompleted(subprojectForUpdate);
        }

        ProjectVisibility visibilityToUpdate = subprojectDto.getVisibility();
        if (Objects.nonNull(visibilityToUpdate)) {
            checkPossibleSetPrivateVisibilitySubproject(subprojectForUpdate, visibilityToUpdate);
            subprojectForUpdate.setVisibility(visibilityToUpdate);
            setVisibilityForChildren(subprojectForUpdate.getChildren(), visibilityToUpdate);
        }

        return subprojectMapper.toDtoReqCreate(subprojectForUpdate);
    }

    private void createMomentWhenSubprojectCompleted(Project subproject) {
        List<Long> userIds = subproject.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream()
                        .map(TeamMember::getUserId))
                .toList();

        Moment newMoment = Moment.builder()
                .name("Выполнены все подпроекты")
                .description("Выполнены все подпроекты")
                .projects(List.of(subproject.getParentProject()))
                .userIds(userIds)
                .build();

        // могу я тут напрямую дергать репозиторий моментов? Или это нарушит принцип SOLID?
        momentRepository.save(newMoment);
    }

    private void setVisibilitySubproject(Project subproject, ProjectVisibility visibility) {
        ProjectVisibility visibilityToSet = Objects.isNull(visibility)
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

    private void setVisibilityForChildren(List<Project> childrenSubprojects, ProjectVisibility visibilityToSet){
        for (Project curChild : childrenSubprojects) {
            curChild.setVisibility(visibilityToSet);
            if (!curChild.getChildren().isEmpty()) {
                setVisibilityForChildren(curChild.getChildren(), visibilityToSet);
            }
        }
    }

    private boolean isParentVisibilityPublic(Project parentProject) {
        return parentProject.getVisibility() == ProjectVisibility.PUBLIC;
    }

    private void setRequiredStatusForChildrenProjects(List<Project> childrenSubprojects, ProjectStatus requiredStatus) {
        if (Objects.nonNull(requiredStatus)) {
            for (Project curChild : childrenSubprojects) {
                curChild.setStatus(requiredStatus);
                if (!curChild.getChildren().isEmpty()) {
                    setRequiredStatusForChildrenProjects(curChild.getChildren(), requiredStatus);
                }
            }
        }
    }

    private void updateStatus(Project project, ProjectStatus status) {
        if (status == ProjectStatus.CANCELLED || status == ProjectStatus.COMPLETED) {
            setRequiredStatusForChildrenProjects(project.getChildren(), status);
        } else {
            project.setStatus(status);
        }
    }

    private void checkPossibilityUpdateSubproject(Project subproject) {
        ProjectStatus parentStatus = subproject.getParentProject().getStatus();
        if (parentStatus == ProjectStatus.CANCELLED || parentStatus == ProjectStatus.COMPLETED) {
            String errorMessage = MessageFormat.format(PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT, parentStatus);
            throw new SubprojectException(errorMessage);
        }
    }

    private boolean isStatusCompletedForAllChildren(Project project) {
        return project.getChildren().stream().map(Project::getStatus).allMatch(status -> status == ProjectStatus.COMPLETED);
    }

    private void checkIsSubproject(Project project) {
        if (Objects.isNull(project.getParentProject())) {
            String errorMessage = MessageFormat.format(PROJECT_IS_NOT_SUBPROJECT_FORMAT, project.getId());
            throw new SubprojectException(errorMessage);
        }
    }
}
