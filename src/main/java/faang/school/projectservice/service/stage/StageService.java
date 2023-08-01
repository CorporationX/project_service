package faang.school.projectservice.service.stage;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageMapper stageMapper;
    private final TaskRepository taskRepository;
    private final StageRolesRepository stageRolesRepository;
    private final UserContext userContext;
    private final StageInvitationRepository stageInvitationRepository;

    @Transactional
    public StageDto create(StageDto stageDto) {
        validateStageProject(stageDto);

        Stage stage = stageMapper.toEntity(stageDto);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }
//Обновить этап.
//
//ProcessStageRoles Если на этап требуется участник с определённой ролью, нужно проверить, что в списке задействованных на этапе участников есть пользователь с такой ролью.
//Если нет, то нужно найти среди участников проекта пользователя с такой ролью и отправить ему приглашение участвовать в этапе.
//Сколько пользователей с данной ролью требуется, столько приглашений разным пользователям должно быть отправлено.
//
//Валидация Если изменяется список участников, нужно проверять, что обновлённые список участников удовлетворяет требованиям ролей.
//Update

    @Transactional
    public StageDto updateStageRoles(StageDto stageDto) {
        validateStageProject(stageDto);
        Stage stageToUpdate = stageRepository.getById(stageDto.getStageId());


        stageRepository.save(stageToUpdate);

        return stageMapper.toDto(stageToUpdate);
    }

    @Transactional
    public void deleteStageWithTasks(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        taskRepository.deleteAll(tasks);
        stageRepository.delete(stage);
    }

    @Transactional
    public void deleteStageCloseTasks(long stageId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();

        tasks.forEach(task -> {
            task.setStatus(TaskStatus.CLOSED);
            taskRepository.save(task);
        });

        stageRepository.delete(stage);
    }

    @Transactional
    public void deleteStageTransferTasks(long stageId, long stageToUpdateId) {
        Stage stage = stageRepository.getById(stageId);
        List<Task> tasks = stage.getTasks();
        Stage stageToUpdate = stageRepository.getById(stageToUpdateId);
        List<Task> updatedTasks = stageToUpdate.getTasks();

        if (updatedTasks == null) {
            updatedTasks = new ArrayList<>();
        }

        updatedTasks.addAll(tasks);
        stageToUpdate.setTasks(updatedTasks);

        stageRepository.save(stageToUpdate);
        stageRepository.delete(stage);
    }

    public List<StageDto> getAllProjectStages(long projectId) {
        List<Stage> stages = projectRepository.getProjectById(projectId).getStages();

        return stages.stream()
                .map(stageMapper::toDto)
                .toList();
    }

    public StageDto getStageById(long stageId) {
        Stage stage = stageRepository.getById(stageId);

        return stageMapper.toDto(stage);
    }

    private void sendStageInvitation(long stageId, long invitedTeamMemberId) {
        TeamMember author = TeamMember.builder().id(userContext.getUserId()).build();
        StageInvitation stageInvitation = StageInvitation.builder()
                .author(author)
                .invited(TeamMember.builder().id(invitedTeamMemberId).build())
                .description("You are invited on the Project stage " + stageId)
                .status(StageInvitationStatus.PENDING)
                .build();

        stageInvitationRepository.save(stageInvitation);
    }

    private int executorsNeeded(StageDto stageDto) {
        return stageDto.getStageRoles().stream()
                .mapToInt(StageRolesDto::getCount)
                .sum();
    }

    private void validateStageProject(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        ProjectStatus projectStatus = project.getStatus();

        if (!projectStatus.equals(ProjectStatus.IN_PROGRESS) && !projectStatus.equals(ProjectStatus.CREATED)) {
            String errorMessage = String.format(
                    "Project %d is %s", project.getId(), projectStatus.name().toLowerCase());

            throw new DataValidationException(errorMessage);
        }
    }
}
