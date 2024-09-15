package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StageInvitationService {

    @Autowired
    private StageInvitationRepository invitationRepository;

    @Autowired
    private StageInvitationMapper mapper;

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    public StageInvitationDto sendInvitation(StageInvitationDto invitationDto) {
        if (invitationDto.getStageId() == null || invitationDto.getAuthorId() == null || invitationDto.getInviteeId() == null) {
            throw new IllegalArgumentException("Author, invitee, and stage must be specified");
        }

        TeamMember author = teamMemberRepository.findById(invitationDto.getAuthorId());

        TeamMember invitee = teamMemberRepository.findById(invitationDto.getInviteeId());

        Stage stage = stageRepository.getById(invitationDto.getStageId());

        StageInvitation newInvitation = new StageInvitation();
        newInvitation.setAuthor(author);
        newInvitation.setInvited(invitee);
        newInvitation.setStage(stage);
        newInvitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitation savedInvitation = invitationRepository.save(newInvitation);

        return mapper.toDto(savedInvitation);
    }

    public StageInvitationDto acceptInvitation(Long invitationId) {
        StageInvitation invitation = invitationRepository.findById(invitationId);

        if (!invitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new IllegalStateException("The invitation cannot be accepted because its current status does not match: " + invitation.getStatus());
        }

        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        Stage stage = invitation.getStage();
        TeamMember invitee = invitation.getInvited();

        if (stage.getExecutors() == null) {
            stage.setExecutors(new ArrayList<>());
        }
        if (!stage.getExecutors().contains(invitee)) {
            stage.getExecutors().add(invitee);
        }
        stageRepository.save(stage);

        StageInvitation updatedInvitation = invitationRepository.save(invitation);
        return mapper.toDto(updatedInvitation);
    }

    public StageInvitationDto declineInvitation(Long invitationId, String reason) {
        StageInvitation invitation = invitationRepository.findById(invitationId);

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason for declining the invitation must be provided.");
        }
        if (!invitation.getStatus().equals(StageInvitationStatus.PENDING)) {
            throw new IllegalStateException("The invitation cannot be declined because its current status does not match: " + invitation.getStatus());
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);

        StageInvitation updatedInvitation = invitationRepository.save(invitation);

        return mapper.toDto(updatedInvitation);
    }

    public List<StageInvitationDto> getInvitationsByUser(Long userId) {
        List<StageInvitation> allInvitations = invitationRepository.findAll();

        List<StageInvitation> filteredInvitations = allInvitations.stream()
                .filter(invitation -> invitation.getInvited().getId().equals(userId))
                .collect(Collectors.toList());

        return filteredInvitations.stream()
                .map(mapper :: toDto)
                .collect(Collectors.toList());
    }
}