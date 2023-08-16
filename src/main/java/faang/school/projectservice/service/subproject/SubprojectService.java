package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoReqCreate;
import faang.school.projectservice.exceptions.SubprojectException;
import faang.school.projectservice.mapper.subproject.SubprojectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

import static faang.school.projectservice.commonMessage.SubprojectErrMessage.ERR_VISIBILITY_PARENT_PROJECT_FORMAT;

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
}
