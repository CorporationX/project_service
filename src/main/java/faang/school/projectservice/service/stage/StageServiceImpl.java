package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final List<StageFilter> stageFilters;
    private final ProjectService projectService;
    private final StageMapper stageMapper;
    private final TaskRepository taskRepository;
    private final StageInvitationRepository stageInvitationRepository;

    @Override
    public StageDto createStage(StageDto stageDto) {
        projectService.getProject(stageDto.projectId());

        Stage stage = stageMapper.toEntity(stageDto);

        return stageMapper.toDto(stageRepository.save(stage));
    }

    @Override
    public List<StageDto> getProjectStages(long projectId, StageFilterDto filterDto) {
        Project project = projectService.getProject(projectId);

        List<Stage> stages = project.getStages();

        List<Stage> stageList = stageFilters
                .stream()
                .filter(stageFilter -> stageFilter.isApplicable(filterDto))
                .reduce(stages.stream(),
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();

        return stageMapper.toStageDtos(stageList);
    }

    @Override
    public void deleteStage(long stageId) {
        Stage stage = stageRepository.getById(stageId);

        List<Task> tasks = stage.getTasks();

        taskRepository.deleteAll(tasks);
        stageRepository.delete(stage);
    }

    @Override
    public StageDto updateStage(long stageId, StageRolesDto stageRolesDto) {
        Stage stage = stageRepository.getById(stageId);

        long neededMembers = getAmountOfMissingMembersWithNeededRole(stage.getExecutors(), stageRolesDto);

        if (neededMembers > 0) {
            stage.getProject().getTeams()
                    .stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(member -> member.getRoles().contains(stageRolesDto.teamRole()))
                    .limit(neededMembers)
                    .forEach(member -> sendInvitation(StageInvitation.builder().stage(stage).build()));
        }

        return stageMapper.toDto(stage);
    }

    @Override
    public List<StageDto> getStages(long projectId) {
        return stageMapper.toStageDtos(projectService.getProject(projectId).getStages());
    }

    @Override
    public StageDto getSpecificStage(long stageId) {
        return stageMapper.toDto(stageRepository.getById(stageId));
    }

    private void sendInvitation(StageInvitation invitation) {
        stageInvitationRepository.save(invitation);
    }

    private long getAmountOfMissingMembersWithNeededRole(List<TeamMember> executors, StageRolesDto stageRolesDto) {
        long stageMembers = executors
                .stream()
                .filter(teamMember -> teamMember.getRoles()
                        .contains(stageRolesDto.teamRole()))
                .count();

        return stageRolesDto.count() - stageMembers;
    }
}
