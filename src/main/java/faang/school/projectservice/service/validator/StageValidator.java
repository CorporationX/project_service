package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageTeamRoleFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageValidator {

    private final ProjectRepository projectRepository;
    private final StageRepository stageRepository;

    public void validateStageCreation(StageDto stageDto) {
        Project project = getValidProject(stageDto.getProjectId());
        if (project.getStatus() == ProjectStatus.CANCELLED) {
            throw new BusinessException("Проект в статусе Отменен");
        }

    }

    public Project getValidProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Проект не найден."));
    }

    public void checkStageForUpdate(Long stageId, StageUpdateDto stageUpdateDto) {
        Project project = getValidProject(stageUpdateDto.getProjectId());
        Stage stage = stageRepository.findById(stageId).orElseThrow(
                ()->new EntityNotFoundException("Этап который обновляем не существует"));

        project.getStages().stream()
                .filter(stage1 -> stage1.getStageId().equals(stageId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Этап с ID " + stageId + " не найден у проекта"));

    }

    public void checkStageToRemove(Long stageId, StageDeleteDto stageDeleteDto) {

        Project project = getValidProject(stageId);
        project.getStages().stream()
                .findFirst().orElseThrow(
                        () -> new EntityNotFoundException("У проекта на найдены этапы"));
    }

}
