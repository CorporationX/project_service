package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StageValidator stageValidator;
    private final StageMapper stageMapper;
    private final List<StageFilter> stageFilters;
    private final TeamMemberRepository teamMemberRepository;
    private final StageRolesRepository stageRolesRepository;

    public StageDto createStage(StageDto stageDto) {

        stageValidator.validateStageCreation(stageDto);
        Stage stage = stageMapper.toEntity(stageDto,taskRepository,stageRolesRepository,teamMemberRepository,projectRepository);
        stageRepository.save(stage);
        return stageMapper.toDto(stage,taskRepository,stageRolesRepository,teamMemberRepository);
    }

    public List<StageDto> getStages(Long projectId) {
        Project project = stageValidator.getValidProject(projectId);
        return project.getStages().stream()
                .map(stage->stageMapper.toDto(stage,taskRepository,stageRolesRepository,teamMemberRepository)).toList();
    }

    public List<StageDto> getActiveStages(long projectId, StageFilterDto stageFilter) {
        Project project = projectRepository.getReferenceById(projectId);
        List<Stage> projectStages = project.getStages();

        return projectStages.stream()
                .filter(stage -> stageFilters.stream()
                        .filter(filter -> filter.isApplicable(stageFilter))
                        .anyMatch(filter -> filter.filterEntity(stage, stageFilter)))
                .map(stage->stageMapper.toDto(stage,taskRepository,stageRolesRepository,teamMemberRepository)).toList();
    }

    public void deleteStage(Long stageId, StageDeleteDto stageDeleteDto) {

        stageValidator.checkStageToRemove(stageId,stageDeleteDto);

        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new EntityNotFoundException("Этап не найден"));
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            List<Long> listTasks = stage.getTasks().stream()
                    .map(Task::getId)
                    .toList();
            executor.submit(() -> taskRepository.deleteAllById(listTasks));
            List<Long> rollesr = stage.getStageRoles().stream()
                    .map(StageRoles::getId)
                    .toList();
            executor.submit(() -> stageRolesRepository.deleteAllById(rollesr));
            List<Long> exc = stage.getExecutors().stream()
                    .map(TeamMember::getId)
                    .toList();
            executor.submit(() -> teamMemberRepository.deleteAllById(exc));
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                throw new RuntimeException("Время ожидания завершения задач истекло");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new RuntimeException("Удаление было прервано", e);
        } finally {
            stageRepository.delete(stage);
        }
    }

    public StageUpdateDto updateStage(Long stageId, StageUpdateDto stageUpdateDto) {

        StageDto stageDto = stageMapper.toStageDto(stageUpdateDto);
        stageValidator.checkStageForUpdate(stageId, stageUpdateDto);
        stageRepository.save(stageMapper.toEntity(stageDto,taskRepository,stageRolesRepository,teamMemberRepository,projectRepository));
        return stageMapper.toStageUpdateDto(stageDto);
    }

    public StageInvitationDto sendInvitations(Long stageId, StageInvitationDto stageInvitationDto) {
        return null;

    }

    public List<Task> getStageTasks(Long stageId, TaskStatus status) {
        return stageRepository.getReferenceById(stageId).getTasks().stream().toList();
    }

    public ResponseEntity<StageUpdateDto> updateStageParticipants(Set<StageUpdateDto> stageUpdateDto) {
        return null;
    }
}
