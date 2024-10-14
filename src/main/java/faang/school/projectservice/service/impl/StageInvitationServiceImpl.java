package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageRoles;
import faang.school.projectservice.model.entity.StageInvitation;
import faang.school.projectservice.model.enums.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.StageInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository invitationRepository;
    private final StageInvitationMapper mapper;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
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

    @Override
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

    @Override
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

    @Override
    public List<StageInvitationDto> getInvitationsByUser(Long userId) {
        return invitationRepository.findAll().stream()
                .filter(invitation -> invitation.getInvited().getId().equals(userId))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createStageInvitation(TeamMember invited, Stage stage, StageRoles stageRoles) {
        StageInvitation stageInvitation = new StageInvitation();
        String INVITATIONS_MESSAGE = String.format("Invite you to participate in the development stage %s " +
                        "of the project %s for the role %s",
                stage.getStageName(), stage.getProject().getName(), stageRoles.getTeamRole());
        stageInvitation.setDescription(INVITATIONS_MESSAGE);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setAuthor(stage.getExecutors().get(0));
        stageInvitation.setInvited(invited);
        stageInvitation.setStage(stage);
        invitationRepository.save(stageInvitation);
    }
}