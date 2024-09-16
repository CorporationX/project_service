package faang.school.projectservice.service.stage_invitation.impl;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationDto invitationDto) {
        Stage stage = stageRepository.findById(invitationDto.stageId())
                .orElseThrow(() -> new EntityNotFoundException("Stage not found with id: " + invitationDto.stageId()));

        TeamMember author = teamMemberRepository.findById(invitationDto.authorId());

        TeamMember invited = teamMemberRepository.findById(invitationDto.invitedId());

        if (stageInvitationRepository.existsByAuthorAndInvitedAndStage(author, invited, stage)) {
            throw new IllegalArgumentException("Invitation already exists.");
        }

        StageInvitation invitation = new StageInvitation();
        invitation.setStage(stage);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        invitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toStageInvitationDto(savedInvitation);
    }

    @Override
    @Transactional
    public StageInvitationDto acceptInvitation(Long id) {
        StageInvitation invitation = stageInvitationRepository.findById(id);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation cannot be accepted in its current status.");
        }

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);

        Stage stage = invitation.getStage();
        TeamMember invited = invitation.getInvited();

        stage.getExecutors().add(invited);
        stageRepository.save(stage);

        return stageInvitationMapper.toStageInvitationDto(updatedInvitation);
    }

    @Override
    @Transactional
    public StageInvitationDto declineInvitation(Long id, String reason) {
        StageInvitation invitation = stageInvitationRepository.findById(id);

        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation cannot be declined in its current status.");
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);

        StageInvitation updatedInvitation = stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toStageInvitationDto(updatedInvitation);
    }

    @Override
    public List<StageInvitationDto> getInvitations(Map<String, String> filters) {
        List<StageInvitation> invitations = stageInvitationRepository.findAll();

        if (filters.containsKey("invitedId")) {
            Long invitedId = Long.parseLong(filters.get("invitedId"));
            invitations = invitations.stream()
                    .filter(invitation -> invitation.getInvited().getId().equals(invitedId))
                    .collect(Collectors.toList());
        }

        if (filters.containsKey("status")) {
            try {
                StageInvitationStatus status = StageInvitationStatus.valueOf(filters.get("status").toUpperCase());
                invitations = invitations.stream()
                        .filter(invitation -> invitation.getStatus() == status)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status value: " + filters.get("status"));
            }
        }

        return invitations.stream()
                .map(stageInvitationMapper::toStageInvitationDto)
                .collect(Collectors.toList());
    }
}