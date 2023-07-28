package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.SubtaskActionDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;

    private final TaskRepository taskRepository;

    private final StageMapper stageMapper;

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    private final StageValidator stageValidator;

    public StageDto createStage(StageDto stageDto) {
        stageValidator.validateStageDtoForProjectCompletedAndCancelled(stageDto);
        Stage entity = stageMapper.toEntity(stageDto);
        Stage stage = stageRepository.save(entity);
        return stageMapper.toDto(stage);
    }

    public void deleteStage(Long oldStageId, SubtaskActionDto methodDeletingStageDto, Long newStageId) {
        Stage stage = stageRepository.getById(oldStageId);
        List<Task> tasks = stage.getTasks();
        deleteBySubtaskAction(methodDeletingStageDto, newStageId, stage, tasks);
        stageRepository.delete(stage);
    }

    private void deleteBySubtaskAction(SubtaskActionDto methodDeletingStageDto, Long newStageId, Stage stage, List<Task> tasks) {
        if (SubtaskActionDto.CASCADE.equals(methodDeletingStageDto)) {
            taskRepository.deleteAll(tasks);
        } else if (SubtaskActionDto.CLOSE.equals(methodDeletingStageDto)) {
            tasks.forEach(task -> task.setStatus(TaskStatus.DONE));
        } else if (SubtaskActionDto.MOVE_TO_NEXT_STAGE.equals(methodDeletingStageDto)) {
            stage.setTasks(List.of());
            Stage stageToAddTasks = stageRepository.getById(newStageId);
            if (stageToAddTasks.getTasks() == null) {
                stageToAddTasks.setTasks(new ArrayList<>());
            }
            stageToAddTasks.getTasks().addAll(tasks);
            stageRepository.save(stageToAddTasks);
        }
    }

    public StageRolesDto updateStageRoles(Long id, StageRolesDto stageRoles) {
        Stage stage = stageRepository.getById(id);
        int countTeamRoles = getTotalTeamRoles(stageRoles, stage);
        if (countTeamRoles >= stageRoles.getCount()) {
            throw new DataValidationException(stageRoles.getTeamRole().name() + " no longer required");
        } else {
            inviteMembersToStage(stageRoles, stage, countTeamRoles);
        }
        return stageRoles;
    }

    private void inviteMembersToStage(StageRolesDto stageRoles, Stage stageById, int countTeamRoles) {
        List<TeamMember> teamMembersInProject = teamMemberJpaRepository.findByProjectId(stageById.getProject().getId());
        teamMembersInProject.stream()
                .filter(teamMember -> teamMember.getStages().stream()
                        .noneMatch(stage -> stage.getStageId().equals(stageById.getStageId())))
                .filter(teamMember -> teamMember.getRoles().contains(stageRoles.getTeamRole()))
                .limit(stageRoles.getCount() - countTeamRoles)
                .forEach(teamMember -> teamMember.getStages().add(stageById));
        changeStageRolesToActual(stageRoles, stageById, countTeamRoles);
        teamMemberJpaRepository.saveAll(teamMembersInProject);
    }

    private void changeStageRolesToActual(StageRolesDto stageRoles, Stage stageById, int countTeamRoles) {
        stageById.getStageRoles().stream()
                .filter(stageRole -> stageRole.getTeamRole().equals(stageRoles.getTeamRole()))
                .findFirst()
                .ifPresent(stageRole -> stageRole.setCount(stageRole.getCount() - countTeamRoles + stageRoles.getCount()));
        stageRepository.save(stageById);
    }

    private int getTotalTeamRoles(StageRolesDto stageRoles, Stage stageById) {
        return stageById.getStageRoles().stream()//количество разработчиков на этапе
                .filter(stageRole -> stageRole.getTeamRole().equals(stageRoles.getTeamRole()))
                .mapToInt(StageRoles::getCount)
                .sum();
    }

    public List<StageDto> getAllStages() {
        return stageRepository.findAll()
                .stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(Long id) {
        return stageMapper.toDto(stageRepository.getById(id));
    }
}
