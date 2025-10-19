package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.AllStageFilterDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
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
    public void createStage(StageCreateDto stageCreateDto) {
        Project project = getProjectOrThrow(stageCreateDto.projectId());
        isApplicableProject(project);
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamMembersId(stageCreateDto.executorsId());
        List<Task> tasks = new ArrayList<>();
        stageRepository.save(stageMapper.toEntityCreate(stageCreateDto, teamMembers, project, tasks));
    }

    @Override
    public List<StageDto> getAllStageByFilter(AllStageFilterDto allStageFilterDto) {
        List <Stage> stageList = stageFilter.applyByRoleAndStatus(allStageFilterDto);
        return stageMapper.toListDto(stageList);
    }

    @Override
    public void deleteStage(StageDeleteDto stageDeleteDto) {
        Stage stage = getStageOrThrow(stageDeleteDto.stageId());
        stageRepository.delete(stage);
    }


    @Transactional
    @Override
    public void updateStage(StageUpdateDto stageUpdateDto) {
     /*   Stage stage = getStageOrThrow(stageUpdateDto.stageId());
        List<TeamMember> teamMembers = stage.getExecutors();
        boolean hasExecutorRole = teamMembers.stream()
                .anyMatch(executor -> executor.getRoles().stream()
                        .anyMatch(teamRole -> teamRole.equals(stageUpdateDto.teamRole())));
        if (hasExecutorRole) {
            teamMembers.stream()
                    .filter(teamMember -> teamMember.getRoles().stream()
                            .anyMatch(executorRole -> executorRole.equals(stageUpdateDto.teamRole())))
                    .map()
        }*/
    }

    @Override
    public List<StageDto> getStages(long projectId) {
        Project project = getProjectOrThrow(projectId);
        return stageMapper.toListDto(project.getStages());
    }

    @Override
    public StageDto getStage(long stageId) {
        return stageMapper.toDto(getStageOrThrow(stageId));
    }

    private Stage getStageOrThrow(long stageId) {
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