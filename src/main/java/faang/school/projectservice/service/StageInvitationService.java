package faang.school.projectservice.service;

import faang.school.projectservice.controller.StageInvitationController;
import faang.school.projectservice.dto.invitation.AcceptInvitationRequest;
import faang.school.projectservice.dto.invitation.AcceptInvitationResponse;
import faang.school.projectservice.dto.invitation.DeclineInvitationRequest;
import faang.school.projectservice.dto.invitation.DeclineInvitationResponse;
import faang.school.projectservice.dto.invitation.GetUserInvitationsRequest;
import faang.school.projectservice.dto.invitation.GetUserInvitationsResponse;
import faang.school.projectservice.dto.invitation.InvitationDto;
import faang.school.projectservice.dto.invitation.SendInvitationRequest;
import faang.school.projectservice.dto.invitation.SendInvitationResponse;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository repository;
    private final StageInvitationController controller;
    private final StageInvitationMapper mapper;

    @Transactional
    public SendInvitationResponse sendInvitation(SendInvitationRequest request) {
        StageInvitation stageInvitation = mapper.toStageInvitation(request);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        return mapper.toSendInvitationResponse(stageInvitation);
    }

    public AcceptInvitationResponse acceptInvitation(AcceptInvitationRequest request) {
        StageInvitation stageInvitation = repository.findById(request.id())
                .orElseThrow(() -> new DataValidationException("Invitation not found"));

        if (!StageInvitationStatus.PENDING.equals(stageInvitation.getStatus())) {
            throw new IllegalArgumentException("Only pending invitations can be accepted");
        }

        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        return AcceptInvitationResponse.builder()
                .id(stageInvitation.getId())
                .status(String.valueOf(stageInvitation.getStatus()))
                .build();
    }

    @Transactional
    public DeclineInvitationResponse declineInvitation(DeclineInvitationRequest request) {
        StageInvitation stageInvitation = repository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        if (!StageInvitationStatus.PENDING.equals(stageInvitation.getStatus())) {
            throw new IllegalStateException("Only pending invitations can be accepted");
        }
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(request.description());

        return DeclineInvitationResponse.builder()
                .id(stageInvitation.getId())
                .status(String.valueOf(stageInvitation.getStatus()))
                .description(stageInvitation.getDescription())
                .build();
    }

    public GetUserInvitationsResponse getUserInvitations(GetUserInvitationsRequest request) {
        List<StageInvitation> invitations = repository.findAll().stream()
                .filter(invitation -> invitation.getInvited().equals(request.invited()))
                .collect(Collectors.toList());

        List<InvitationDto> invitationDtos = invitations.stream()
                .map(mapper::toInvitationDto)
                .collect(Collectors.toList());

        return GetUserInvitationsResponse.builder()
                .invitations(invitationDtos)
                .build();
    }


}
