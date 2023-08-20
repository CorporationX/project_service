package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoReqCreate;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.exception.SubprojectException;
import faang.school.projectservice.mapper.subproject.SubprojectMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
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

    public SubprojectDtoReqCreate updateSubproject(SubprojectUpdateDto subprojectDto) {
// ���� �� ��������� ��� � ������������� ������� ������ �������� ������, � � ���� ������� � ���������� ������.
        // ������� ��������� � ������ �������, �.� ���� ������������ ������ ������, �� ������ ������ ������.
        Project subprojectForUpdate = projectRepository.getProjectById(subprojectDto.getSubprojectId());
        checkIsSubproject(subprojectForUpdate);

        ProjectStatus statusToUpdate = subprojectDto.getStatus();
        if (statusToUpdate != null) {
            updateStatus(subprojectForUpdate, statusToUpdate);
        }
        if (isStatusForAllChildrenClosed(subprojectForUpdate)) {
            // �������� �����
            List<Team> teams = subprojectForUpdate.getTeams();
            teams.
        }
    }

    private void setVisibilitySubproject(Project subproject, ProjectVisibility visibility) {
        ProjectVisibility visibilityToSet = Objects.isNull(visibility)
                ? ProjectVisibility.PUBLIC
                : visibility;

        if (isParentVisibilityPublic(subproject.getParentProject()) && visibility == ProjectVisibility.PRIVATE) {
            String errorMessage = MessageFormat.format(ERR_VISIBILITY_PARENT_PROJECT_FORMAT, visibility);
            throw new SubprojectException(errorMessage);
        }

        subproject.setVisibility(visibilityToSet);
    }

    private boolean isParentVisibilityPublic(Project parentProject) {
        return parentProject.getVisibility() == ProjectVisibility.PUBLIC;
    }

    //��� ���� ���������, ��� ���������� �������� ���������� ����� ��� �� ������. �.�. ������ ������� ������,
    // ���� � ���� ���� ��� ��� �������� ����������. ����� ������� ��������� ��� ����������,
    // � ������ ����� ������������ ������
    private void setRequiredStatusForChildrenProjects(List<Project> childrenProjects, ProjectStatus requiredStatus) {
        if (Objects.nonNull(requiredStatus)) {
            for (Project curProject : childrenProjects) {
                curProject.setStatus(requiredStatus);
                if (!curProject.getChildren().isEmpty()) {
                    setRequiredStatusForChildrenProjects(curProject.getChildren(), requiredStatus);
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

    // ���������, � ���� � ������� ��������� ��� ��� ����������, ����� �������� Moment,
    // � ��������� ������� ���������� ����������� �������.
    // ����������, ��� � ������� ���� ������ ���������� ��� �����������.
    private boolean isStatusForAllChildrenClosed(Project project) {
        return project.getChildren().stream().map(Project::getStatus).allMatch(status -> status == ProjectStatus.CANCELLED);
    }

    private void checkIsSubproject(Project project) {
        if (Objects.isNull(project.getParentProject())) {
            String errorMessage = MessageFormat.format(PROJECT_IS_NOT_SUBPROJECT_FORMAT, project.getId());
            throw new SubprojectException(errorMessage);
        }
    }
}
