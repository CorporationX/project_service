package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageFilterDto;
import faang.school.projectservice.dto.StageRoleDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRoleMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage.TasksAfterDelete;
import faang.school.projectservice.repository.StageRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Stream;

@Validated
@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final StageMapper stageMapper;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final StageRoleMapper stageRoleMapper;
    private final StageRolesService stageRolesService;
    private final List<StageFilter> filters;

    public StageDto createStage(@Valid StageDto stageDto) {
        Project project = projectService.getProjectById(stageDto.getProjectId());
        ProjectStatus currentStatus = project.getStatus();
        validatedProjectStatus(currentStatus);

        Stage stage = stageMapper.toEntity(stageDto);
        stage.setProject(project);
        List<Long> teamMemberIds = stageDto.getTeamMemberIds();
        List<TeamMember> teamMembers = teamMemberService.findAllById(teamMemberIds);
        stage.setExecutors(teamMembers);

        teamMembers.forEach(teamMember -> {
            if (teamMember.getRoles() == null) {
                throw new DataValidationException("Team member with id: " + teamMember.getId() + " has no role");
            }
        });

        List<StageRoles> stageRolesList = stageRoleMapper.toEntityList(stageDto.getStageRoles());
        stageRolesList.forEach(stageRoles -> stageRoles.setStage(stage));
        stage.setStageRoles(stageRolesList);
        Stage saveStage = stageRepository.save(stage);
        stageRolesService.saveAll(stageRolesList);
        List<StageRoleDto> stageRolesDtoList = stageRoleMapper.toDtoList(stageRolesList);
        StageDto saveStageDto = stageMapper.toDto(saveStage);
        saveStageDto.setStageRoles(stageRolesDtoList);
        return saveStageDto;
    }

    private static void validatedProjectStatus(ProjectStatus currentStatus) {
        if (currentStatus.equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException(ExceptionMessage.PROJECT_CANCELED);
        }
        if (currentStatus.equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException(ExceptionMessage.PROJECT_COMPLETED);
        }
    }

    public List<StageDto> getFilteredStages(StageFilterDto filterDto) {
        Stream<Stage> stages = stageRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(stages,
                        (stages1, filter) -> filter.apply(stages1, filterDto),
                        Stream::concat)
                .map(stageMapper::toDto)
                .toList();
    }

    public void deleteStage(Long deletedStageId, TasksAfterDelete tasksAfterDelete, Long receivingStageId) {
        switch (tasksAfterDelete) {
            case CLOSING -> closeTasksAfterDeleteStage(deletedStageId);

            case CASCADE_DELETE -> deleteTasksAfterDeleteStage(deletedStageId);

            case TRANSFER_TO_ANOTHER_STAGE -> transferTasksAfterDeleteStage(deletedStageId, receivingStageId);
        }
    }

    private void closeTasksAfterDeleteStage(Long id) {
        Stage deletedStage = stageRepository.getById(id);
        deletedStage.getTasks()
                .forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        stageRepository.delete(deletedStage);
    }

    private void deleteTasksAfterDeleteStage(Long id) {
        Stage deletedStage = stageRepository.getById(id);
        List<Task> deletedTasks = deletedStage.getTasks();
        taskRepository.deleteAll(deletedTasks);
        stageRepository.delete(deletedStage);
    }

    private void transferTasksAfterDeleteStage(Long deletedStageId, Long receivingStageId) {
        if (receivingStageId == null) {
            throw new DataValidationException("Id cannot be null");
        }
        Stage deletedStage = stageRepository.getById(deletedStageId);
        List<Task> transferredTasks = deletedStage.getTasks();
        stageRepository.getById(receivingStageId).setTasks(transferredTasks);
        stageRepository.delete(deletedStage);
    }

    public StageDto updateStage(@Valid StageDto stageDto) {
        Project project = projectService.getProjectById(stageDto.getProjectId());
        Stage stage = stageMapper.toEntity(stageDto);
        stage.setProject(project);
        List<Long> teamMemberIds = stageDto.getTeamMemberIds();
        List<TeamMember> teamMembers = teamMemberService.findAllById(teamMemberIds);
        stage.setExecutors(teamMembers);

        List<StageRoles> stageRolesList = stageRoleMapper.toEntityList(stageDto.getStageRoles());
        stage.setStageRoles(stageRolesList);
        stageRolesList.forEach(
                stageRoles -> stageRolesService.getExecutorsForRole(stage, stageRoles));
        stage.setStageRoles(stageRolesList);
        Stage updatedStage = stageRepository.save(stage);
        stageRolesService.saveAll(stageRolesList);
        List<StageRoleDto> stageRolesDtoList = stageRoleMapper.toDtoList(stageRolesList);
        StageDto updetedStageDto = stageMapper.toDto(updatedStage);
        updetedStageDto.setStageRoles(stageRolesDtoList);
        return updetedStageDto;
    }

    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stageMapper.toDtoList(stages);
    }

    public StageDto getStage(Long id) {
        Stage stage = stageRepository.getById(id);
        return stageMapper.toDto(stage);
    }
}