package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage_inavation.StageInvitationDto;
import faang.school.projectservice.exception.InvalidInvitationStateException;
import faang.school.projectservice.exception.InvitationAlreadyExistsException;
import faang.school.projectservice.mapper.stage_inavation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional

public class StageInvitationService {

    private final StageInvitationRepository invitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper invitationMapper;

    public StageInvitationDto createInvitation(StageInvitationDto invitationDto) {
        Stage stage = getStageById(invitationDto.stageId());
        TeamMember author = getTeamMemberById(invitationDto.authorId());
        TeamMember invited = getTeamMemberById(invitationDto.invitedId());

        checkInvitationExists(stage, invited);

        StageInvitation invitation = buildInvitation(invitationDto, stage, author, invited);

        StageInvitation savedInvitation = invitationRepository.save(invitation);
        return invitationMapper.toDto(savedInvitation);
    }

    public StageInvitationDto acceptInvitation(Long invitationId) {
        StageInvitation invitation = findInvitationById(invitationId);

        validateInvitationStatus(invitation, StageInvitationStatus.PENDING, "Only pending invitations can be accepted.");

        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        addExecutorToStage(invitation.getStage(), invitation.getInvited());

        StageInvitation updatedInvitation = invitationRepository.save(invitation);
        return invitationMapper.toDto(updatedInvitation);
    }

    public StageInvitationDto declineInvitation(Long invitationId, String reason) {
        StageInvitation invitation = findInvitationById(invitationId);

        validateInvitationStatus(invitation, StageInvitationStatus.PENDING, "Only pending invitations can be declined.");

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);

        StageInvitation updatedInvitation = invitationRepository.save(invitation);
        return invitationMapper.toDto(updatedInvitation);
    }

    public List<StageInvitationDto> getInvitations(Long participantId, String status) {
        List<StageInvitation> invitations = fetchInvitations(participantId, status);
        return invitations.stream()
                .map(invitationMapper::toDto)
                .collect(Collectors.toList());
    }

    public StageInvitationDto getInvitationById(Long invitationId) {
        StageInvitation invitation = findInvitationById(invitationId);
        return invitationMapper.toDto(invitation);
    }

    private Stage getStageById(Long stageId) {
        return stageRepository.getById(stageId);
    }

    private TeamMember getTeamMemberById(Long memberId) {
        return teamMemberRepository.findById(memberId);
    }

    private void checkInvitationExists(Stage stage, TeamMember invited) {
        if (invitationRepository.existsByStageAndInvited(stage, invited)) {
            throw new InvitationAlreadyExistsException("Invitation already exists for this stage and invited member.");
        }
    }

    private StageInvitation buildInvitation(StageInvitationDto dto, Stage stage, TeamMember author, TeamMember invited) {
        return StageInvitation.builder()
                .stage(stage)
                .author(author)
                .invited(invited)
                .description(dto.description())
                .status(StageInvitationStatus.PENDING)
                .build();
    }

    private StageInvitation findInvitationById(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found by id: " + invitationId));
    }

    private void validateInvitationStatus(StageInvitation invitation, StageInvitationStatus expectedStatus, String errorMessage) {
        if (invitation.getStatus() != expectedStatus) {
            throw new InvalidInvitationStateException(errorMessage);
        }
    }

    private void addExecutorToStage(Stage stage, TeamMember executor) {
        stage.getExecutors().add(executor);
        stageRepository.save(stage);
    }

    private List<StageInvitation> fetchInvitations(Long participantId, String status) {
        if (participantId != null && status != null) {
            StageInvitationStatus invitationStatus = parseStatus(status);
            return invitationRepository.findByInvitedIdAndStatus(participantId, invitationStatus);
        } else if (participantId != null) {
            return invitationRepository.findByInvitedId(participantId);
        } else if (status != null) {
            StageInvitationStatus invitationStatus = parseStatus(status);
            return invitationRepository.findByStatus(invitationStatus);
        } else {
            return invitationRepository.findAll();
        }
    }

    private StageInvitationStatus parseStatus(String status) {
        try {
            return StageInvitationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParameterException("Invalid status value: " + status);
        }
    }
}