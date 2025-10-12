package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.dto.stage.StageRequestDeleteDto;
import faang.school.projectservice.dto.stage.StageRequestUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.StageFilterImpl;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;


@RequiredArgsConstructor
@Service
public class StageServiceImpl implements StageService {
    private static final TaskStatus TRANSFER_TASK_STATUS = TaskStatus.IN_PROGRESS;
    private final StageMapper stageMapper;
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageFilterImpl stageFilter;

    @Transactional
    @Override
    public void createStage(StageRequestCreateDto stageRequestCreateDto) {
        validateStageCreateDto(stageRequestCreateDto);
        stageMapper.toEntity(stageRequestCreateDto);
    }

    @Override
    public List<Stage> getAllStageByFilter(StageRequestCreateDto stageRequestCreateDto, TeamRole teamRole, TaskStatus taskStatus) {
        return stageFilter.applyByRoleAndStatus(stageRequestCreateDto.project(), teamRole, taskStatus);
    }

    @Transactional
    @Override
    public void deleteStage(StageRequestDeleteDto stageRequestDeleteDto) {
        Project project = stageRequestDeleteDto.project();
        Optional<Stage> stageForDelete = project.getStages().stream()
                .filter(stage -> stage.getStageId().equals(stageRequestDeleteDto.stage().getStageId()))
                .findFirst();
        if (stageForDelete.isEmpty()) {
            throw new EntityNotFoundException("Такого этапа не существует!");
        }
        List<Task> tasks = stageForDelete.get().getTasks();
        stageRepository.delete(stageForDelete.get());
        project.getStages()
                .removeIf(stage -> stage.getStageId().equals(stageRequestDeleteDto.stage().getStageId()));
        Stage stage = project.getStages().stream()
                .filter(stages -> stages.getTasks().stream()
                        .anyMatch(taskList -> taskList.getStatus().equals(TRANSFER_TASK_STATUS)))
                .findFirst()
                .orElseThrow();
        stage.getTasks().addAll(tasks);
        project.getStages().add(stage);
        projectRepository.save(project);
    }

    @Transactional
    @Override
    public void updateStage(StageRequestUpdateDto stageRequestUpdateDto) {
        List<TeamMember> teamMembers = stageRequestUpdateDto.stage().getExecutors();
        boolean hasExecutorRole = teamMembers.stream()
                .anyMatch(executor -> executor.getRoles().stream()
                        .anyMatch(teamRole -> teamRole.equals(stageRequestUpdateDto.teamRole())));
        if (hasExecutorRole) {
            teamMembers.stream()
                    .anyMatch()
        }
    }

    @Override
    public void getAllStage() {

    }

    @Override
    public void getStageById() {

    }

    private void validateStageCreateDto(StageRequestCreateDto stageRequestCreateDto) {
        if (stageRequestCreateDto.teamRoles().isEmpty()) {
            throw new EntityNotFoundException("Необходимо указать список ролей!");
        }
        if (stageRequestCreateDto.executors().isEmpty()) {
            throw new EntityNotFoundException("Необходимо указать участников этапа!");
        }
        Project project = stageRequestCreateDto.project();
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new ForbiddenException("Нельзя создать этап к отмененному или завершенному проекту!");
        }
        //Валидация что в списке участников этапа нет лишних (не задействованных) пользователей.
    }
}