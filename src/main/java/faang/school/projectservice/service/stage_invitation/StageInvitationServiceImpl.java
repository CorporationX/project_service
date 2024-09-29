package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StageInvitationServiceImpl implements StageInvitationService {

    private final StageInvitationRepository repository;
    private final StageInvitationMapper mapper;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public StageInvitationDto sendInvitation(StageInvitationDto dto) {
        StageInvitation invitation = mapper.toEntity(dto);
        invitation.setStatus(StageInvitationStatus.PENDING);
        repository.save(invitation);
        return mapper.toDto(invitation);
    }

    @Override
    public StageInvitationDto acceptInvitation(Long invitationId, Long userId) {
        StageInvitation invitation = repository.findById(invitationId);

        TeamMember invitedUser = teamMemberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!invitation.getInvited().getId().equals(userId)) {
            throw new IllegalArgumentException("This user was not invited to this stage.");
        }

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        repository.save(invitation);

        return mapper.toDto(invitation);
    }

    @Override
    public StageInvitationDto declineInvitation(Long invitationId, Long userId, String reason) {
        StageInvitation invitation = repository.findById(invitationId);

        TeamMember invitedUser = teamMemberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!invitation.getInvited().getId().equals(userId)) {
            throw new IllegalArgumentException("This user was not invited to this stage.");
        }

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);
        repository.save(invitation);

        return mapper.toDto(invitation);
    }

    @Override
    public List<StageInvitationDto> getUserInvitations(Long userId) {
        return repository.findAll().stream()
                .filter(invitation -> invitation.getInvited() != null && invitation.getInvited().getId().equals(userId))
                .map(mapper::toDto)
                .toList();
    }
}
