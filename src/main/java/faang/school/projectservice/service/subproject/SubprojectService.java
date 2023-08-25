package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.GeneralSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectFilterDto;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.exception.SubprojectException;
import faang.school.projectservice.filter.subproject.SubprojectFilter;
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
import java.util.stream.Stream;

import static faang.school.projectservice.messages.SubprojectErrMessage.ERR_VISIBILITY_PARENT_PROJECT_FORMAT;
import static faang.school.projectservice.messages.SubprojectErrMessage.PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT;
import static faang.school.projectservice.messages.SubprojectErrMessage.PROJECT_IS_NOT_SUBPROJECT_FORMAT;
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
    private final List<SubprojectFilter> subprojectFilters;

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

        log.info(MessageFormat.format("Starting updating subproject with id:{0}", subprojectId));
        checkPossibilityUpdateSubproject(subprojectForUpdate);

        ProjectStatus statusToUpdate = subprojectDto.getStatus();
        if (nonNull(statusToUpdate)) {
            updateStatus(subprojectForUpdate, statusToUpdate);
        }

        if (subprojectForUpdate.getStatus() == ProjectStatus.COMPLETED &&
                isStatusCompletedForAllChildren(subprojectForUpdate)) {
            createMomentWhenSubprojectCompleted(subprojectForUpdate);
        }

        ProjectVisibility visibilityToUpdate = subprojectDto.getVisibility();
        if (nonNull(visibilityToUpdate)) {
            checkPossibleSetPrivateVisibilitySubproject(subprojectForUpdate, visibilityToUpdate);
            subprojectForUpdate.setVisibility(visibilityToUpdate);
            setVisibilityForChildren(subprojectForUpdate.getChildren(), visibilityToUpdate);
        }

        log.info("Subproject with id:{0} ready to save");
        return subprojectMapper.toGeneralDto(projectRepository.save(subprojectForUpdate));
    }

    @Transactional(readOnly = true)
    public List<Long> getAllSubprojectByFilter(Long subprojectId, SubprojectFilterDto filterDto) {
        Project currentSubproject = getSubprojectFromRepository(subprojectId);
        List<SubprojectFilter> listApplicableFilters = subprojectFilters.stream()
                .filter(curFilter -> curFilter.isApplicable(filterDto))
                .toList();

        log.info(MessageFormat.format("Filtering of child subprojects for subproject id:{0} started", subprojectId));

        if (listApplicableFilters.isEmpty()) {
            return Collections.emptyList();
        } else {
            Stream<Project> childrenProjects = currentSubproject.getChildren()
                    .stream()
                    .filter(child -> child.getVisibility().equals(ProjectVisibility.PUBLIC));

            for (SubprojectFilter curFilter : listApplicableFilters) {
                childrenProjects = curFilter.apply(childrenProjects, filterDto);
            }
            return childrenProjects.map(Project::getId).toList();
        }
    }

    private Project getSubprojectFromRepository(Long subprojectId) {
        Project subproject = projectRepository.getProjectById(subprojectId);
        checkIsSubproject(subproject);
        return subproject;
    }

    private void createMomentWhenSubprojectCompleted(Project subproject) {
        List<Team> teams = Optional.ofNullable(subproject.getTeams())
                .orElse(Collections.emptyList());

        List<Long> uniqueUserIds = teams
                .stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream()
                        .map(TeamMember::getUserId))
                .distinct()
                .toList();

        Moment newMoment = Moment.builder()
                .name("Выполнены все подпроекты")
                .description("Выполнены все подпроекты")
                .projects(List.of(subproject.getParentProject()))
                .userIds(uniqueUserIds)
                .date(LocalDateTime.now())
                .build();

        // могу я тут напрямую дергать репозиторий моментов? Или это нарушит принцип SOLID?
        momentRepository.save(newMoment);
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

    private void setVisibilityForChildren(List<Project> childrenSubprojects, ProjectVisibility visibilityToSet) {
        if (nonNull(childrenSubprojects)) {
            for (Project curChild : childrenSubprojects) {
                curChild.setVisibility(visibilityToSet);
                if (nonNull(curChild.getChildren())) {
                    setVisibilityForChildren(curChild.getChildren(), visibilityToSet);
                }
            }
        }
    }

    private boolean isParentVisibilityPublic(Project parentProject) {
        return parentProject.getVisibility() == ProjectVisibility.PUBLIC;
    }

    private void setRequiredStatusForChildrenProjects(List<Project> childrenSubprojects, ProjectStatus requiredStatus) {
        if (nonNull(childrenSubprojects)) {
            for (Project curChild : childrenSubprojects) {
                curChild.setStatus(requiredStatus);
                if (nonNull(curChild.getChildren())) {
                    setRequiredStatusForChildrenProjects(curChild.getChildren(), requiredStatus);
                }
            }
        }
    }

    private void updateStatus(Project project, ProjectStatus status) {
        if (status == ProjectStatus.CANCELLED || status == ProjectStatus.COMPLETED) {
            setRequiredStatusForChildrenProjects(project.getChildren(), status);
        }
        project.setStatus(status);
    }

    private void checkPossibilityUpdateSubproject(Project subproject) {
        ProjectStatus parentStatus = subproject.getParentProject().getStatus();
        if (parentStatus == ProjectStatus.CANCELLED || parentStatus == ProjectStatus.COMPLETED) {
            String errorMessage = MessageFormat.format(PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT, parentStatus);
            throw new SubprojectException(errorMessage);
        }
    }

    private boolean isStatusCompletedForAllChildren(Project project) {
        List<Project> children = Optional.ofNullable(project.getChildren()).orElse(new ArrayList<>());
        return children.stream().map(Project::getStatus).allMatch(status -> status == ProjectStatus.COMPLETED);
    }

    private void checkIsSubproject(Project project) {
        if (isNull(project.getParentProject())) {
            String errorMessage = MessageFormat.format(PROJECT_IS_NOT_SUBPROJECT_FORMAT, project.getId());
            throw new SubprojectException(errorMessage);
        }
    }
}