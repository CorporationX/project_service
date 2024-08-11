package faang.school.projectservice.service.stage;

import faang.school.projectservice.controller.stage.StageWithTasksAction;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.stage.StageServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.projectservice.model.TaskStatus.DONE;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageService {
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final StageInvitationRepository stageInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageMapper stageMapper;
    private final StageServiceValidator stageServiceValidator;

    public StageDto createStage(StageDto stageDto) {
        var stage = stageMapper.toEntity(stageDto);
        var project = projectRepository.getProjectById(stageDto.getProjectId());
        stage.setProject(project);
        stageServiceValidator.validate(stage);
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public List<StageDto> getAllByStatus(ProjectStatus status) {
        return projectRepository.findAllByStatus(status).stream()
                .flatMap(project -> project.getStages().stream().map(stageMapper::toDto))
                .toList();
    }

    public void handle(long id, StageWithTasksAction action, Long stageToMoveId) {
        var stage = stageRepository.getById(id);
        var tasks = stage.getTasks();
        switch (action) {
            case CASCADE -> tasks.forEach(taskRepository::delete);
            case CLOSE -> {
                tasks.forEach(task -> task.setStatus(DONE));
                tasks.forEach(taskRepository::save);
            }
            case TRANSFER -> {
                var stageToMove = stageRepository.getById(stageToMoveId);
                stageToMove.setTasks(Stream.concat(stageToMove.getTasks().stream(), tasks.stream()).toList());
                stageRepository.save(stageToMove);
            }
        }
        stageRepository.delete(stage);
    }

    public StageDto update(long userId, long stageId, StageDto stageDto) {
        var stage = stageMapper.toEntity(stageDto);
        stage.setStageId(stageId);
        var allRolesInExecutors = stage.getExecutors().stream()
                .flatMap(executor -> executor.getRoles().stream())
                .collect(Collectors.toSet());
        var project = projectRepository.getProjectById(stage.getProject().getId());
        var allProjectTeamMembers = new ArrayList<>(project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()).toList());
        for (StageRoles stageRole: stage.getStageRoles()) {
            if (allRolesInExecutors.contains(stageRole.getTeamRole())) {
                continue;
            }
            var counter = stageRole.getCount();
            for (TeamMember member: allProjectTeamMembers) {
                if (!stage.getExecutors().contains(member) &&
                        member.getRoles().contains(stageRole.getTeamRole())) {
                    counter--;
                    allProjectTeamMembers.remove(member);
                    var invitation = StageInvitation.builder().description("Приглашение на этап проекта")
                            .status(StageInvitationStatus.PENDING)
                            .stage(stage)
                            .author(teamMemberRepository.findByUserIdAndProjectId(userId, project.getId()))
                            .invited(member).build();
                    stageInvitationRepository.save(invitation);
                    if (counter == 0)
                        break;
                }
            }
            if (counter != 0)
                throw new RuntimeException("В проекте не хватает работников для заполнения выполняющих на этапе");
        }
        return stageMapper.toDto(stageRepository.save(stage));
    }

    public List<StageDto> getAll(long projectId) {
        return projectRepository.getProjectById(projectId).getStages().stream().map(stageMapper::toDto).toList();
    }

    public StageDto getById(long id) {
        return stageMapper.toDto(stageRepository.getById(id));
    }
}
