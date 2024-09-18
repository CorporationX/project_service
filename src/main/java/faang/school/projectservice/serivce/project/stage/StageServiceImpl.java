package faang.school.projectservice.serivce.project.stage;

import faang.school.projectservice.dto.project.stage.RemoveTypeDto;
import faang.school.projectservice.dto.project.stage.StageCreateDto;
import faang.school.projectservice.dto.project.stage.StageDto;
import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.dto.project.stage.StageUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.project.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.serivce.project.stage.filters.StageFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.exception.ExceptionMessages.EXECUTOR_NOT_FOUNT;
import static faang.school.projectservice.exception.ExceptionMessages.MIGRATE_STAGE_ID_IS_REQUIRED;
import static faang.school.projectservice.exception.ExceptionMessages.PROJECT_NOT_FOUND;
import static faang.school.projectservice.exception.ExceptionMessages.WRONG_PROJECT_STATUS;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;

    @Override
    public StageDto createStage(StageCreateDto stageCreateDto) {
        validateProject(stageCreateDto.projectId());
        Project project = projectRepository.getProjectById(stageCreateDto.projectId());
        if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
            throw new DataValidationException(
                    WRONG_PROJECT_STATUS.getMessage()
                            .formatted(stageCreateDto.projectId()));
        }
        Stage newStage = stageMapper.toStage(stageCreateDto);
        newStage.getStageRoles().forEach(role -> role.setStage(newStage));
        return stageMapper.toStageDto(stageRepository.save(newStage));
    }

    @Override
    public List<StageDto> getStages(Long projectId, StageFilterDto filters) {
        validateProject(projectId);
        List<Stage> stages = stageRepository.findAll().stream()
                .filter(stage -> stage.getProject().getId().equals(projectId))
                .toList();
        List<Stage> filteredStages = filterStages(stages.stream(), filters);
        return stageMapper.toStageDtos(filteredStages);
    }

    @Override
    public StageDto removeStage(Long stageId, RemoveTypeDto removeTypeDto) {
        Stage stage = stageRepository.getById(stageId);
        switch (removeTypeDto.removeAction()) {
            case CASCADE_DELETE:
                removeStageWithTasks(stage);
                break;
            case CLOSE:
                removeStageWithClosingTasks(stage);
                break;
            case MIGRATE:
                removeStageWithMigrateTasks(stage, removeTypeDto);
                break;
        }
        return stageMapper.toStageDto(stage);
    }

    @Override
    public StageDto updateStage(StageUpdateDto stageUpdateDto, Long userId) {
        Stage stage = stageRepository.getById(stageUpdateDto.stageId());
        if (stageUpdateDto.stageName() != null) {
            stage.setStageName(stageUpdateDto.stageName());
        }
        List<TeamMember> executors = getExecutors(stageUpdateDto.executorIds());
        stage.setExecutors(executors);
        checkMemberCountForRole(stage, executors, userId);
        return stageMapper.toStageDto(stageRepository.save(stage));
    }

    @Override
    public StageDto getStage(Long stageId) {
        Stage stage = stageRepository.getById(stageId);
        return stageMapper.toStageDto(stage);
    }

    private List<Stage> filterStages(Stream<Stage> stages, StageFilterDto filters) {
        for (StageFilter filter : stageFilters) {
            if (filter.isApplicable(filters)) {
                filter.apply(stages, filters);
            }
        }
        return stages.toList();
    }

    private void removeStageWithTasks(Stage stage) {
        stageRepository.delete(stage);
    }

    private void removeStageWithClosingTasks(Stage stage) {
        stage.getTasks().forEach(task -> {
            task.setStatus(TaskStatus.CANCELLED);
            task.setStage(null);
        });
        stageRepository.save(stage);
        stageRepository.delete(stage);
    }

    private void removeStageWithMigrateTasks(Stage stage, RemoveTypeDto removeTypeDto) {
        if (removeTypeDto.stageForMigrateId() == null) {
            throw new DataValidationException(MIGRATE_STAGE_ID_IS_REQUIRED.getMessage());
        }
        Stage stageForMigrate = stageRepository.getById(removeTypeDto.stageForMigrateId());
        if (stageForMigrate.getTasks() == null) {
            stageForMigrate.setTasks(new ArrayList<>());
        }
        stageForMigrate.getTasks().addAll(stage.getTasks());
        stageRepository.save(stageForMigrate);
        stageRepository.delete(stage);
    }

    private List<TeamMember> getExecutors(List<Long> executorIds) {
        List<TeamMember> executors = new ArrayList<>();
        for (Long executorId : executorIds) {
            try {
                TeamMember executor = teamMemberRepository.findById(executorId);
                executors.add(executor);
            } catch (jakarta.persistence.EntityNotFoundException ex) {
                throw new EntityNotFoundException(EXECUTOR_NOT_FOUNT.getMessage().formatted(executorId));
            }
        }
        return executors;
    }

    private void checkMemberCountForRole(Stage stage, List<TeamMember> executors, Long userId) {
        for (StageRoles role : stage.getStageRoles()) {
            long executorWithNeededRoleCount = executors.stream().
                    filter(executor -> executor.getRoles().contains(role.getTeamRole()))
                    .count();
            long neededCount = role.getCount() - executorWithNeededRoleCount;
            if (neededCount > 0) {
                projectRepository.getProjectById(stage.getProject().getId()).getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .filter(teamMember -> !executors.contains(teamMember))
                        .filter(teamMember -> teamMember.getRoles().contains(role.getTeamRole()))
                        .limit(neededCount)
                        .forEach(teamMember -> sendInvitation(stage, teamMember, userId));
            }
        }
    }

    private void sendInvitation(Stage stage, TeamMember executor, Long userId) {
//        stageInvitationServiceImpl.sendInvitation(StageInvitationDto.builder()
//                .stageId(stage.getStageId())
//                .authorId(userId)
//                .invitedId(executor.getId())
//                .build());
//        что-то типо такого
    }

    private void validateProject(Long projectId) {
        if (projectRepository.existsById(projectId) == false) {
            throw new EntityNotFoundException(PROJECT_NOT_FOUND.getMessage().formatted(projectId));
        }
    }
}
