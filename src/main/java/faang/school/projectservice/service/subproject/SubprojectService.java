package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoReqCreate;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.exceptions.SubprojectException;
import faang.school.projectservice.mapper.subproject.SubprojectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

import static faang.school.projectservice.commonMessage.SubprojectErrMessage.*;

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
// Надо ли проверять что у родительского проекта статус например закрыт, а я хочу сделать у подпроекта открыт.
        // логично проверить и кинуть эксепшн, т.е если родительский проект закрыт, то ничего менять нельзя.
        Project subprojectForUpdate = projectRepository.getProjectById(subprojectDto.getSubprojectId());
        checkIsSubproject(subprojectForUpdate);

        ProjectStatus statusToUpdate = subprojectDto.getStatus();
        if (statusToUpdate != null) {
            updateStatus(subprojectForUpdate, statusToUpdate);
        }
        if (isStatusForAllChildrenClosed(subprojectForUpdate)) {
            // получить момет
            List<Team> teams = subprojectForUpdate.getTeams();
            teams.
        }
    }

    private void setVisibilitySubproject(Project subproject, ProjectVisibility visibility) {
        if (visibility == null) {
            subproject.setVisibility(ProjectVisibility.PUBLIC);
            return;
        }
        if (isParentVisibilityPublic(subproject.getParentProject()) && visibility == ProjectVisibility.PRIVATE) {
            String errorMessage = MessageFormat.format(ERR_VISIBILITY_PARENT_PROJECT_FORMAT, visibility);
            throw new SubprojectException(errorMessage);
        }
        subproject.setVisibility(visibility);
    }

    private boolean isParentVisibilityPublic(Project parentProject) {
        return parentProject.getVisibility() == ProjectVisibility.PUBLIC;
    }

    //При этом проверить, все подпроекты текущего подпроекта имеют тот же статус. Т.е. нельзя закрыть проект,
    // если у него есть все ещё открытые подпроекты. Нужно сначала закрывать все подпроекты,
    // и только потом родительский проект
    private void setRequiredStatusForChildrenProjects(List<Project> childrenProjects, ProjectStatus requiredStatus) {
        if (requiredStatus != null) {
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
        ProjectStatus parentStatus = (subproject.getParentProject().getStatus();
        if (parentStatus == ProjectStatus.CANCELLED || parentStatus == ProjectStatus.COMPLETED) {
            String errorMessage = MessageFormat.format(PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT, parentStatus);
            throw new SubprojectException(errorMessage);
        }
    }

    // Проверить, и если у проекта закрылись все его подпроекты, тогда получаем Moment,
    // а участники проекта становятся участниками момента.
    // Получается, что у проекта есть момент “Выполнены все подпроекты”.
    private boolean isStatusForAllChildrenClosed(Project project) {
        return project.getChildren().stream().map(Project::getStatus).allMatch(status -> status == ProjectStatus.CANCELLED);
    }


    private void checkIsSubproject(Project project) {
        if (project.getParentProject() == null) {
            String errorMessage = MessageFormat.format(PROJECT_IS_NOT_SUBPROJECT_FORMAT, project.getId());
            throw new SubprojectException(errorMessage);
        }
    }
}
