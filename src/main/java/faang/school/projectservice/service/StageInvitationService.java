package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage_invitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.RejectStageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFiltersDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageService stageService;
    private final StageInvitationMapper stageInvitationMapper;
    private final TeamMemberService teamMemberService;
    private final StageInvitationValidator stageInvitationValidator;

    private final List<Filter<StageInvitation, StageInvitationFiltersDto>> stageInvitationFilters;

    public StageInvitationDto sendStageInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateStageInvitation(stageInvitationDto);

        StageInvitation stageInvitation = StageInvitation
                .builder()
                .stage(stageService.getStageById(stageInvitationDto.getStageId()))
                .author(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getAuthorId()))
                .invited(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getInvitedId()))
                .status(StageInvitationStatus.PENDING)
                .build();

        stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto acceptStageInvitation(AcceptStageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(stageInvitation);
        stageService.setExecutor(stageInvitation.getStage().getStageId(), stageInvitation.getInvited().getId());

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto rejectStageInvitation(RejectStageInvitationDto rejectStageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(rejectStageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(rejectStageInvitationDto.getDescription());

        stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> filters(StageInvitationFiltersDto filters) {
        Stream<StageInvitation> stageInvitation = stageInvitationRepository.findAll().stream();

        Stream<StageInvitation> filteredGoals = stageInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        stageInvitation,
                        (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s1
                );

        return filteredGoals.map(stageInvitationMapper::toDto).toList();
    }

    public StageInvitationDto createStageInvitationDto(Stage stage, TeamMember teamMember) {
        var stageInvitationDto = StageInvitationDto.builder()
                .stageId(stage.getStageId())
                .authorId(stage.getProject().getOwnerId())
                .invitedId(teamMember.getId())
                .build();
        stageInvitationValidator.validateStageInvitation(stageInvitationDto);
        return stageInvitationDto;
    }

    public void sendStageInvitationToProjectParticipants(Stage stage,
                                                         List<TeamMember> teamMembers,
                                                         int requiredNumberOfUsers) {
        int numberInvitationsToSend = Math.min(requiredNumberOfUsers, teamMembers.size());

        for (int i = 0; i < numberInvitationsToSend; i++) {
            StageInvitationDto invitation = createStageInvitationDto(stage, teamMembers.get(i));
            sendStageInvitation(invitation);
        }
    }
}
