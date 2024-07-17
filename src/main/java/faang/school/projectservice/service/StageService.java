package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.StageException;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.FateOfTasksAfterDelete;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationRepository stageInvitationRepository;
    private final TaskRepository taskRepository;
    private final StageMapper stageMapper;
    private final List<StageFilter> filters;

    public StageDto createStage(StageDto stageDto) {
        Project project = projectRepository.getProjectById(stageDto.getProjectId());
        ProjectStatus currentStatus = project.getStatus();
        validatedProjectStatus(currentStatus);

        Stage stage = stageMapper.toEntity(stageDto);
        stage.setProject(project);
        List<TeamMember> executors = stageDto.getExecutorIds()
                .stream()
                .map(teamMemberRepository::findById)
                .toList();
        stage.setExecutors(executors);

        executors.forEach(teamMember -> {
            if (teamMember.getRoles().isEmpty()) {
                throw new StageException("Team member with id: " + teamMember.getId() + "has no role");
            }
        });
        Stage saveStage = stageRepository.save(stage);
        return stageMapper.toDto(saveStage);
    }

    private static void validatedProjectStatus(ProjectStatus currentStatus) {
        if (currentStatus.equals(ProjectStatus.CANCELLED)) {
            throw new StageException(ErrorMessage.PROJECT_CANCELED);
        }
        if (currentStatus.equals(ProjectStatus.COMPLETED)) {
            throw new StageException(ErrorMessage.PROJECT_COMPLETED);
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

    public void deleteStage(Long deletedStageId, FateOfTasksAfterDelete tasksAfterDelete, Long receivingStageId) {
        switch (tasksAfterDelete) {
            case CLOTHING -> closeTasksAfterDeleteStage(deletedStageId);

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
        deletedTasks.forEach(task -> taskRepository.deleteById(task.getId()));
        deletedTasks.clear();
        stageRepository.delete(deletedStage);
    }

    private void transferTasksAfterDeleteStage(Long deletedStageId, Long receivingStageId) {
        if(receivingStageId == null){
            throw new StageException(ErrorMessage.NULL_ID);
        }
        Stage deletedStage = stageRepository.getById(deletedStageId);
        List<Task> transferedTasks = deletedStage.getTasks();
        stageRepository.getById(receivingStageId).setTasks(transferedTasks);
        transferedTasks.clear();
        stageRepository.delete(deletedStage);
    }

    public StageDto updateStage(StageDto stageDto) {
        Stage stage = stageMapper.toEntity(stageDto);
        stage.getStageRoles().forEach(
                stageRoles -> getExecutorsForRole(stage, stageRoles));
        Stage updatedStage = stageRepository.save(stage);
        return stageMapper.toDto(updatedStage);
    }

    private void getExecutorsForRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = new ArrayList<>();
        stage.getExecutors().forEach(executor -> {
            if (executor.getRoles()
                    .contains(stageRoles.getTeamRole())) {
                executorsWithTheRole.add(executor);
            }
        });

        List<TeamMember> projectMembersWithTheSameRole = new ArrayList<>();
        if (executorsWithTheRole.size() < stageRoles.getCount()) {
            stage.getProject()
                    .getTeams()
                    .forEach(team ->
                            projectMembersWithTheSameRole.addAll(
                                    team.getTeamMembers()
                                            .stream()
                                            .filter(teamMember ->
                                                    !teamMember.getStages().contains(stage))
                                            .filter(teamMember ->
                                                    teamMember.getRoles()
                                                            .contains(stageRoles.getTeamRole()))
                                            .toList()));
        }

        int requiredNumberOfInvitation = stageRoles.getCount() - executorsWithTheRole.size();

        if (projectMembersWithTheSameRole.size() < requiredNumberOfInvitation) {
            projectMembersWithTheSameRole.forEach(teamMember -> {
                StageInvitation stageInvitationToSend = getStageInvitation(teamMember, stage, stageRoles);
                stageInvitationRepository.save(stageInvitationToSend);
            });

            int numberOfMissingTeamMembers = requiredNumberOfInvitation - projectMembersWithTheSameRole.size();

            throw new StageException("To work at the project stage, " + stageRoles.getCount() +
                    " participants with a role " + stageRoles + " are required. But there are only " +
                    projectMembersWithTheSameRole.size() + " participants on the project with this role. " +
                    " There is a need for two more participants." + numberOfMissingTeamMembers);
        }

        projectMembersWithTheSameRole.stream()
                .limit(requiredNumberOfInvitation)
                .forEach(teamMember -> {
                    StageInvitation stageInvitationToSend = getStageInvitation(teamMember, stage, stageRoles);
                    stageInvitationRepository.save(stageInvitationToSend);
                });
    }

    private StageInvitation getStageInvitation(TeamMember invited, Stage stage, StageRoles stageRoles) {
        StageInvitation stageInvitationToSend = new StageInvitation();
        String INVITATIONS_MESSAGE = String.format("Invite you to participate in the development stage %s " +
                "of the project %s for the role %s", stage.getStageName(), stage.getProject().getName(), stageRoles);
        stageInvitationToSend.setDescription(INVITATIONS_MESSAGE);
        stageInvitationToSend.setStatus(StageInvitationStatus.PENDING);
        stageInvitationToSend.setStage(stage);
        stageInvitationToSend.setInvited(invited);
        return stageInvitationToSend;
    }

    public List<StageDto> getAllStages() {
        List<Stage> stages = stageRepository.findAll();
        return stageMapper.toDto(stages);
    }

    public StageDto getStage(Long id) {
        Stage stage = stageRepository.getById(id);
        return stageMapper.toDto(stage);
    }
}
