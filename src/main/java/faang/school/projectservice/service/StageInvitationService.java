package faang.school.projectservice.service;

import faang.school.projectservice.controller.StageInvitationController;
import faang.school.projectservice.dto.FilterDto.StageInvitationFilterDto;
import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.filter.invitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationController stageInvitationController;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageMapper;
    private final StageInvitationValidator stageInvitationValidator;
    private final List<StageInvitationFilter> stageInvitationFilters;


    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = new StageInvitation();
        Stage stage = stageRepository.findById(stageInvitationDto.getStageId())
                .orElseThrow(() -> new IllegalArgumentException("Stage not found"));
        TeamMember author = teamMemberRepository.findById(stageInvitationDto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        TeamMember invited = teamMemberRepository.findById(stageInvitationDto.getInvitedId())
                .orElseThrow(() -> new IllegalArgumentException("Invited not found"));
        invitation.setStage(stage);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        stageInvitationRepository.save(invitation);
        return stageMapper.toDto(invitation);


    }

    public StageInvitationDto acceptInvitation(long invitationId) {
        StageInvitation invitation = stageInvitationRepository.getReferenceById(invitationId);
        TeamMember invited = invitation.getInvited();
        stageInvitationValidator.validateStatusPendingCheck(invitation);

        invited.getStages().add(invitation.getStage());
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(invitation);

        return stageMapper.toDto(invitation);

    }

    public StageInvitationDto rejectStageInvitation(Long id, String rejectionReason) {
        StageInvitation invitation = stageInvitationRepository.getReferenceById(id);
        TeamMember invited = invitation.getInvited();
        stageInvitationValidator.validateStatusPendingCheck(invitation);

        invited.getStages().remove(invitation.getStage());
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setRejectionReason(rejectionReason);
        stageInvitationRepository.save(invitation);
        return stageMapper.toDto(invitation);
    }

    public List<StageInvitationDto> getAllInvitationsForOneParticipant(Long participantId,
                                                                    StageInvitationFilterDto filter) {
        Stream<StageInvitation> stageInvitationFiltered = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getId().equals(participantId));
        return stageInvitationFilters.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(filter))
                .flatMap(stageInvitationFilter -> stageInvitationFilter.apply(stageInvitationFiltered, filter))
                .map(stageMapper::toDto)
                .toList();
    }
}
