package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.DeleteStageDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.project.ProjectException;
import faang.school.projectservice.exception.stage.StageException;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import  org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    @Transactional
    public StageDto createStage(StageDto stageDto) {
        Stage stage = stageRepository.save(stageMapper.toStage(validStage(stageDto)));
        return stageMapper.toStageDto(stage);
    }

    @Transactional
    public void deleteStage(Long stageId1, DeleteStageDto deleteStageDto, Long stageId2) {
        Stage stage = stageRepository.getById(stageId1);
        List<Task> tasks = stage.getTasks();
        if (deleteStageDto.equals(DeleteStageDto.CASCADE)) {
            taskRepository.deleteAll(tasks);
        }
        if (deleteStageDto.equals(DeleteStageDto.CLOSE)) {
            tasks.forEach(task -> task.setStatus(TaskStatus.DONE));
        }
        if (deleteStageDto.equals(DeleteStageDto.MOVE_TO_ANOTHER_STAGE)) {
            stage.setTasks(List.of());
            Stage newStage = stageRepository.getById(stageId2);
            newStage.setTasks(tasks);
            stageRepository.save(newStage);
        }
        stageRepository.delete(stage);
    }

    @Transactional(readOnly = true)
    public List<StageDto> getAllStage(ProjectDto projectDto) {
        if (projectDto.getName().isBlank()) {
            throw new ProjectException("Name cannot be empty");
        }
        if (projectDto.getProjectStatus() == null) {
            throw new ProjectException("The project must have a status");
        }
        projectMapper.toProjectDto(projectRepository.getProjectById(projectDto.getId()));
        return projectDto.getStageList();
    }

    @Transactional(readOnly = true)
    public StageDto getStageById(Long id) {
        Stage stage = stageRepository.getById(id);
        return stageMapper.toStageDto(stage);
    }

    @Transactional(readOnly = true)
    public List<StageDto> filterByStatus(StageFilterDto stageFilterDto) {
        List<Stage> stages = stageRepository.findAll();
        Stream<Stage> projectStream = stages.stream();
        return stageFilters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(stageFilterDto))
                .flatMap(stageFilter -> stageFilter.apply(projectStream, stageFilterDto))
                .map(stageMapper :: toStageDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public void updateStageRoles(Long id, StageRolesDto stageRoles) {
        Stage stage = stageRepository.getById(id);
        int countTeamRoles = getTotalTeamRoles(stageRoles, stage);
        if (countTeamRoles >= stageRoles.getCount()) {
            throw new StageException(stageRoles.getTeamRole().name() + " no longer required");
        } else {
            invitationMembersToStage(stageRoles, stage, countTeamRoles);
        }
    }

    private void invitationMembersToStage(StageRolesDto stageRoles, Stage stageById, int countTeamRoles) {
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
        return stageById.getStageRoles().stream()
                .filter(stageRole -> stageRole.getTeamRole().equals(stageRoles.getTeamRole()))
                .mapToInt(StageRoles::getCount)
                .sum();
    }

    private StageDto validStage(StageDto stage) {
        if (stage.getStageId() == null) {
            throw new StageException("Invalid ID");
        }
        if (stage.getStageName().isBlank()) {
            throw new StageException("Name cannot be empty");
        }

        Project project = projectRepository.getProjectById(stage.getProject().getId());
        boolean projectInProgress = project.getStatus().equals(ProjectStatus.IN_PROGRESS);
        boolean projectCreated = project.getStatus().equals(ProjectStatus.CREATED);

        if (projectInProgress || projectCreated) {
            return stageMapper.toStageDto(stageRepository.getById(stage.getStageId()));
        } else {
            throw new ProjectException(MessageFormat.format("Project with id {0} unavailable", project.getId()));
        }
    }
}
