package faang.school.projectservice.service.stage;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.stage.StageInvitationMapper;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.mapper.stage.StageRolesMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.TaskService;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageService {
    private final StageRepository stageRepository;
    private final ProjectRepository projectRepository;
    private final StageInvitationService stageInvitationService;
    private final UserContext userContext;
    private final StageMapper stageMapper;
    private final StageRolesMapper stageRolesMapper;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageValidator stageValidator;
    private final TaskService taskService;
    private final List<Filter<Stage, StageFilterDto>> filters;

    public StageDto create(StageDto stageDto) {
        stageValidator.validateStageOnCreate(stageDto);

        var stage = stageMapper.toEntity(stageDto);
        Stage stageSaved = stageRepository.save(stage);

        return stageMapper.toDto(stageSaved);
    }

    public StageDto remove(long id) {
        var stageDto = getStage(id);
        var stage = stageMapper.toEntity(stageDto);

        taskService.cancelTasks(id);
        stageRepository.delete(stage);

        return stageDto;
    }

    public StageDto update(Long stageId, StageRolesDto stageRolesDto) {
        var stage = stageRepository.getById(stageId);
        var stageRoles = stage.getStageRoles();

        inviteMembers(stage, stageRolesDto);

        stageRoles.add(stageRolesMapper.toEntity(stageRolesDto));
        var updatedStageRoles = stageRoles.stream()
                .filter(stageRole -> !stageRole.getId().equals(stageRolesDto.getId()))
                .toList();
        stage.setStageRoles(updatedStageRoles);
        stageRepository.save(stage);

        return stageMapper.toDto(stage);
    }

    public StageDto getStage(long id) {
        return stageMapper.toDto(stageRepository.getById(id));
    }

    public List<StageDto> getAllStages(Long projectId) {
        var project = projectRepository.getProjectById(projectId);
        var stages = project.getStages();

        return stages.stream().map(stageMapper::toDto).toList();
    }

    public List<StageDto> getStagesFiltered(Long projectId, StageFilterDto stageFilterDto) {
        var stages = getAllStages(projectId).stream().map(stageMapper::toEntity);
        var filteredStages = filters.stream()
                .filter(filter -> filter.isApplicable(stageFilterDto))
                .reduce(stages,
                        (stream, filter) ->
                                filter.apply(stream, stageFilterDto),
                        Stream::concat)
                .toList();

        return filteredStages.stream().map(stageMapper::toDto).toList();
    }

    private void inviteMembers( Stage stage, StageRolesDto stageRolesDto) {
        var project = projectRepository.getProjectById(stage.getProject().getId());
        var projectMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();
        var teamMembersCount = projectMembers.stream()
                .filter(teamMember -> teamMember
                        .getStages().contains(stage))
                .filter(teamMember -> teamMember
                        .getRoles().contains(stageRolesDto.getTeamRole())
                )
                .toList().size();
        var executorsCount = stageRolesDto.getCount() - teamMembersCount;

        if (stageRolesDto.getCount() > teamMembersCount) {
            var teamMembers = projectMembers.stream()
                    .filter(teamMember -> !stage.getStageRoles().contains(teamMember))
                    .filter(teamMember -> teamMember.getRoles().contains(stageRolesDto.getTeamRole()))
                    .distinct()
                    .toList();

            for (TeamMember teamMember : teamMembers) {
                if (executorsCount == 0) break;
                sendStageInvitation(stage, teamMember);
                executorsCount--;
            }
        }
    }

    private StageInvitationDto sendStageInvitation(Stage stage, TeamMember teamMember) {
        var invitationAuthor = TeamMember.builder()
                .id(userContext.getUserId())
                .build();
        var invitation = StageInvitation.builder()
                .description(stage.getStageName() + " stage invitation")
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .author(invitationAuthor)
                .invited(teamMember)
                .build();

        return stageInvitationService.create(stageInvitationMapper.toDto(invitation));
    }
}
