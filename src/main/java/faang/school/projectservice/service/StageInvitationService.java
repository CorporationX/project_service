package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.filterDto.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.invitation.StageInvitationFilter;
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
    private final TeamMemberService teamMemberService;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidator;
    private final List<StageInvitationFilter> invitationFilters;

    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        Stage stage = stageService.getStageById(stageInvitationDto.getStageId());
        TeamMember invited = teamMemberService.getTeamMemberById(stageInvitationDto.getInvitedId());
        return createStageInvitationAndGetDto(stage, stageInvitationDto.getAuthorId(), invited);
    }

    public StageInvitationDto createStageInvitationAndGetDto(Stage stage, Long authorId, TeamMember invited) {
        TeamMember author = teamMemberService.getTeamMemberById(authorId);
        stageInvitationValidator.validateInvitedForCreate(author.getId(), invited.getId());

        StageInvitation invitation = StageInvitation.builder()
                .stage(stage)
                .author(author)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .build();

        StageInvitation saved = stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toDto(saved);
    }

    public StageInvitationDto acceptStageInvitation(long invitationId) {
        StageInvitation invitation = stageInvitationRepository.getReferenceById(invitationId);
        TeamMember invited = invitation.getInvited();
        stageInvitationValidator.validateStatusPendingCheck(invitation);

        invited.getStages().add(invitation.getStage());
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation saved = stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(saved);

    }

    public StageInvitationDto rejectStageInvitation(Long id, String rejectionReason) {
        if (rejectionReason.isBlank()) {
            throw new DataValidationException("There must be a reason for rejecting an invitation.");
        }
        StageInvitation invitation = stageInvitationRepository.getReferenceById(id);
        TeamMember invited = invitation.getInvited();
        stageInvitationValidator.validateStatusPendingCheck(invitation);

        invited.getStages().remove(invitation.getStage());
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setRejectionReason(rejectionReason);
        StageInvitation saved = stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toDto(saved);
    }

    public List<StageInvitationDto> viewAllInvitation(Long userId, StageInvitationFilterDto filter) {
        List<StageInvitation> allInvitationOfInvited = stageInvitationRepository.findAll().stream()
                .filter(invitation -> invitation.getInvited().getId().equals(userId))
                .toList();

        return filterInvitation(allInvitationOfInvited, filter);
    }

    private List<StageInvitationDto> filterInvitation(List<StageInvitation> invitations,
                                                      StageInvitationFilterDto filter) {
        Stream<StageInvitation> invitationStream = invitations.stream();

        return invitationFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .flatMap(f -> f.apply(invitationStream, filter))
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}
