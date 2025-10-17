package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageRequestAllStageDto;
import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.dto.stage.StageRequestDeleteDto;
import faang.school.projectservice.dto.stage.StageRequestUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class StageServiceImpl implements StageService {
    private static final TaskStatus TRANSFER_TASK_STATUS = TaskStatus.IN_PROGRESS;
    private final StageMapper stageMapper;
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageFilter stageFilter;
    private final TeamMemberRepository teamMemberRepository;
    //private final StageInvitationServiceImpl stageInvitationService;
    //private final StageInvitationMapper stageInvitationMapper;

    @Transactional
    @Override
    public void createStage(StageRequestCreateDto stageRequestCreateDto) {
        Project project = getProjectOrThrow(stageRequestCreateDto.projectId());
        isApplicableProject(project);
        List<TeamMember> executors = stageRequestCreateDto.executorsId().stream()
                .filter(Objects::nonNull)
                .map(teamMemberId -> teamMemberRepository.findByUserIdAndProjectId(teamMemberId, project.getId()))
                .filter(Objects::nonNull)
                .toList();
        List<Task> tasks = new ArrayList<>();

        stageRepository.save(stageMapper.toEntityCreate(stageRequestCreateDto, executors, project, tasks));
    }

    @Override
    public List<Stage> getAllStageByFilter(StageRequestAllStageDto stageRequestAllStageDto) {
        Specification specification = stageFilter

        /*
        getProjectOrThrow(stageRequestAllStageDto.projectId());
        return stageFilter.applyByRoleAndStatus(stageRequestAllStageDto);*/
    }

    @Transactional
    @Override
    public void deleteStage(StageRequestDeleteDto stageRequestDeleteDto) {
        getProjectOrThrow(stageRequestDeleteDto.projectId());
        Project project = projectRepository.findById(stageRequestDeleteDto.projectId()).get();
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
                    .filter(teamMember -> teamMember.getRoles().stream()
                            .anyMatch(executorRole -> executorRole.equals(stageRequestUpdateDto.teamRole())))
                    .map()
        }
    }

    @Transactional
    @Override
    public List<Stage> getAllStage(long projectId) {
        List<Stage> stages = stageRepository.findAll();
        return stages.stream()
                .filter(stage -> stage.getProject().getId().equals(projectId))
                .toList();
    }

    @Override
    public Stage getStageById(long stageId) {
        return stageRepository.findById(stageId).orElseThrow(
                () -> new EntityNotFoundException("По такому id этапа нет!"));
    }

    private Project getProjectOrThrow(long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new EntityNotFoundException("Такого проекта не существует!"));
    }

    //Валидация не лишний ли участник в Этапе
    private void executorSuperfluous(Stage stage) {
        Set<TeamRole> requiredRoles = stage.getStageRoles().stream()
                .map(StageRoles::getTeamRole)
                .collect(Collectors.toSet());

        boolean isExtra = stage.getExecutors().stream()
                .anyMatch(executor ->
                        executor.getRoles().stream()
                                .anyMatch(role -> !requiredRoles.contains(role)));
        if (isExtra) {
            throw new DataValidationException("В этапе находяться лишние пользователи!");
        }
    }

    private void isApplicableProject(Project project) {
        if (project.getStatus().equals(ProjectStatus.CANCELLED) || project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new ForbiddenException("Нельзя создать этап к отмененному или завершенному проекту!");
        }
    }
}