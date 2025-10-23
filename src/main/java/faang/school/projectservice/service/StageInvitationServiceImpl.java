package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.CreateStageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository invitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;

    @Transactional
    @Override
    public StageInvitationDto sendInvitation(CreateStageInvitationDto stageInvitationDto) {
        log.info("Attempting to send stage invitation: authorUserId={}, invitedUserId={}, stageId={}",
                stageInvitationDto.authorUserId(), stageInvitationDto.invitedUserId(), stageInvitationDto.stageId());
        Stage stage = stageRepository.findById(stageInvitationDto.stageId())
                .orElseThrow(() -> {
                    log.error("Stage with id {} not found", stageInvitationDto.stageId());
                    return new EntityNotFoundException("Stage with id " + stageInvitationDto.stageId()
                            + " is not found");
                });

        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(
                stageInvitationDto.authorUserId(), stage.getProject().getId());

        if (author == null) {
            log.error("Author team member not found for userId={} in projectId={}",
                    stageInvitationDto.authorUserId(), stage.getProject().getId());
            throw new EntityNotFoundException("Author team member not found for user: " +
                    stageInvitationDto.authorUserId());
        }

        TeamMember invited = teamMemberRepository.findByUserIdAndProjectId(
                stageInvitationDto.invitedUserId(), stage.getProject().getId());

        if (invited == null) {
            log.error("Invited team member not found for userId={} in projectId={}",
                    stageInvitationDto.invitedUserId(), stage.getProject().getId());
            throw new EntityNotFoundException("Invited team member not found for user: "
                    + stageInvitationDto.invitedUserId());
        }

        StageInvitation invitation = StageInvitation.builder()
                .author(author)
                .invited(invited)
                .stage(stage)
                .description(stageInvitationDto.description())
                .status(StageInvitationStatus.PENDING)
                .build();

        StageInvitation savedInvitation = invitationRepository.save(invitation);

        log.info("Stage invitation successfully created: " +
                        "invitationId={}, stageId={}, authorUserId={}, invitedUserId={}",
                savedInvitation.getId(),
                stage.getStageId(),
                stageInvitationDto.authorUserId(),
                stageInvitationDto.invitedUserId());

        return stageInvitationMapper.toInvitationDto(savedInvitation);
    }

    @Transactional
    @Override
    public StageInvitationDto acceptInvitation(Long invitationId) {
        log.info("Attempting to accept stage invitation with id={}", invitationId);
        StageInvitation invitation = findInvitation(invitationId);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            log.warn("Cannot accept invitation with id={} because it has status={}",
                    invitationId, invitation.getStatus());
            throw new IllegalStateException("Cannot accept invitation with status: " + invitation.getStatus());
        }

        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        StageInvitation updatedInvitation = invitationRepository.save(invitation);
        Stage stage = invitation.getStage();
        TeamMember invitedMember = invitation.getInvited();

        if (!stage.getExecutors().contains(invitedMember)) {
            stage.getExecutors().add(invitedMember);
            stageRepository.save(stage);

            log.info("Added user {} as executor to stage {}", invitedMember.getUserId(), stage.getStageId());
        } else {
            log.debug("User {} is already an executor of stage {}", invitedMember.getUserId(), stage.getStageId());
        }

        log.info("Stage invitation accepted successfully: invitationId={}", invitationId);

        return stageInvitationMapper.toInvitationDto(updatedInvitation);
    }

    @Transactional
    @Override
    public StageInvitationDto rejectInvitation(Long invitationId, String reason) {
        log.info("Attempting to reject stage invitation with id={} and reason='{}'", invitationId, reason);
        StageInvitation invitation = findInvitation(invitationId);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            log.warn("Cannot reject invitation with id={} because it has status={}",
                    invitationId, invitation.getStatus());
            throw new IllegalStateException("Cannot reject invitation with status: " + invitation.getStatus());
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);
        StageInvitation updatedInvitation = invitationRepository.save(invitation);

        log.info("Stage invitation rejected: id={}, reason={}", invitationId, reason);

        return stageInvitationMapper.toInvitationDto(updatedInvitation);
    }

    @Transactional(readOnly = true)
    @Override
    public List<StageInvitationDto> getInvitationsForUser(Long userId, StageInvitationStatus status) {
        log.info("Fetching invitations for userId={}, statusFilter={}", userId, status);

        List<StageInvitationDto> result = invitationRepository.findAll().stream()
                .filter(inv -> inv.getInvited().getUserId().equals(userId))
                .filter(inv -> status == null || inv.getStatus() == status)
                .map(stageInvitationMapper::toInvitationDto)
                .toList();

        log.info("Found {} invitations for userId={} with statusFilter={}", result.size(), userId, status);
        return result;
    }

    private StageInvitation findInvitation(Long invitationId) {
        log.debug("Looking up StageInvitation with id={}", invitationId);
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> {
                    log.error("StageInvitation not found with id={}", invitationId);
                    return new EntityNotFoundException("StageInvitation not found with id: " + invitationId);
                });
    }
}
