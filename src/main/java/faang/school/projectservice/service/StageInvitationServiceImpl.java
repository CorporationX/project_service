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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository invitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;

    @Override
    public StageInvitationDto sendInvitation(CreateStageInvitationDto stageInvitationDto) {
        Stage stage = stageRepository.findById(stageInvitationDto.stageId())
                .orElseThrow(() -> new EntityNotFoundException("Stage with id "
                        + stageInvitationDto.stageId() + " is not found"));

        TeamMember author = teamMemberRepository.findByUserIdAndProjectId(
                stageInvitationDto.authorUserId(), stage.getProject().getId());

        if (author == null) {
            throw new EntityNotFoundException("Author team member not found for user: " +
                    stageInvitationDto.authorUserId());
        }

        TeamMember invited = teamMemberRepository.findByUserIdAndProjectId(
                stageInvitationDto.invitedUserId(), stage.getProject().getId());

        if (invited == null) {
            throw new EntityNotFoundException("Invited team member not found for user: " + stageInvitationDto.invitedUserId());
        }

        StageInvitation invitation = StageInvitation.builder()
                .author(author)
                .invited(invited)
                .stage(stage)
                .description(stageInvitationDto.description())
                .status(StageInvitationStatus.PENDING)
                .build();

        StageInvitation savedInvitation = invitationRepository.save(invitation);
        log.info("Stage invitation sent: id={}, stageId={}, authorUserId={}, invitedUserId={}",
                savedInvitation.getId(),
                stage.getStageId(),
                stageInvitationDto.authorUserId(),
                stageInvitationDto.invitedUserId());

        return stageInvitationMapper.toInvitationDto(savedInvitation);
    }

    @Override
    public StageInvitationDto acceptInvitation(Long invitationId) {
        StageInvitation invitation = findInvitation(invitationId);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Cannot accept invitation with status: " + invitation.getStatus());
        }

        invitation.setStatus(StageInvitationStatus.ACCEPTED);

        StageInvitation updatedInvitation = invitationRepository.save(invitation);
        Stage stage = invitation.getStage();
        TeamMember invitedMember = invitation.getInvited();

        if (!stage.getExecutors().contains(invitedMember)) {
            stage.getExecutors().add(invitedMember);
            stageRepository.save(stage);

            log.info("User {} added as executor to stage {}",
                    invitedMember.getUserId(), stage.getStageId());
        } else {
            log.info("User {} is already executor of stage {}",
                    invitedMember.getUserId(), stage.getStageId());
        }

        log.info("Stage invitation accepted: id={}", invitationId);

        return stageInvitationMapper.toInvitationDto(updatedInvitation);
    }

    @Override
    public StageInvitationDto rejectInvitation(Long invitationId, String reason) {
        StageInvitation invitation = findInvitation(invitationId);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Cannot reject invitation with status: " + invitation.getStatus());
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);
        StageInvitation updatedInvitation = invitationRepository.save(invitation);

        log.info("Stage invitation rejected: id={}, reason={}", invitationId, reason);

        return stageInvitationMapper.toInvitationDto(updatedInvitation);
    }

    @Override
    public List<StageInvitationDto> getInvitationsForUser(Long userId, StageInvitationStatus status) {
        List<StageInvitation> allInvitations = invitationRepository.findAll();

        return allInvitations.stream()
                .filter(invitation -> invitation.getInvited().getUserId().equals(userId))
                .filter(invitation -> status == null || invitation.getStatus() == status)
                .map(stageInvitationMapper::toInvitationDto)
                .collect(Collectors.toList());
    }


    private StageInvitation findInvitation(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("StageInvitation not found with id: " + invitationId));
    }
}
