package faang.school.projectservice.service.invitation;

import faang.school.projectservice.dto.invitation.AcceptInvitationRequest;
import faang.school.projectservice.dto.invitation.AcceptInvitationResponse;
import faang.school.projectservice.dto.invitation.DeclineInvitationRequest;
import faang.school.projectservice.dto.invitation.DeclineInvitationResponse;
import faang.school.projectservice.dto.invitation.InvitationDto;
import faang.school.projectservice.dto.invitation.SendInvitationRequest;
import faang.school.projectservice.dto.invitation.SendInvitationResponse;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.invitation.filter.InvitationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository repository;
    private final StageInvitationMapper mapper;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final List<InvitationFilter> filters;

    @Transactional
    public SendInvitationResponse sendInvitation(SendInvitationRequest request) {
        Stage stage = stageRepository.findById(request.stageId())
                .orElseThrow(() -> new EntityNotFoundException("Stage not found"));

        boolean alreadyExecutor = stage.getExecutors().stream()
                .anyMatch(member -> member.getUserId().equals(request.invited()));
        if (alreadyExecutor) {
            throw new EntityNotFoundException("User is already an executor of this stage");
        }

        StageInvitation stageInvitation = mapper.toStageInvitation(request);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        return mapper.toSendInvitationResponse(stageInvitation);
    }

    @Transactional
    public AcceptInvitationResponse acceptInvitation(AcceptInvitationRequest request) {
        StageInvitation stageInvitation = validatePendingInvitation(request.id());

        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        Stage stage = stageInvitation.getStage();
        stage.getExecutors().add(stageInvitation.getInvited());

        return AcceptInvitationResponse.builder()
                .id(stageInvitation.getId())
                .status(stageInvitation.getStatus().toString())
                .build();
    }

    @Transactional
    public DeclineInvitationResponse declineInvitation(DeclineInvitationRequest request) {
        StageInvitation stageInvitation = validatePendingInvitation(request.id());

        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(request.description());

        return DeclineInvitationResponse.builder()
                .id(stageInvitation.getId())
                .status(String.valueOf(stageInvitation.getStatus()))
                .description(stageInvitation.getDescription())
                .build();
    }

    @Transactional(readOnly = true)
    public List<InvitationDto> getFilteredInvitations(InvitationDto filter) {
        Stream<StageInvitation> invitationsStream = repository.findAll().stream();

        for (InvitationFilter filterHandler : filters) {
            if (filterHandler.isApplicable(filter)) {
                invitationsStream = filterHandler.apply(invitationsStream, filter);
            }
        }

        return invitationsStream
                .map(mapper::toInvitationDto)
                .toList();
    }

    private StageInvitation validatePendingInvitation(Long invitationId) {
        StageInvitation stageInvitation = findEntityById(invitationId);

        if (!StageInvitationStatus.PENDING.equals(stageInvitation.getStatus())) {
            throw new IllegalStateException("Only pending invitations can be accepted or rejected");
        }
        return stageInvitation;
    }

    private StageInvitation findEntityById(Long invitationId) {
        StageInvitation stageInvitation = repository.findById(invitationId)
                .orElseThrow(() -> new DataValidationException("Invitation not found"));

        return stageInvitation;
    }
}
