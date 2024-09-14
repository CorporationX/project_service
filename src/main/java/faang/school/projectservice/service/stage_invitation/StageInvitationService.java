package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StageInvitationService {

    private final StageInvitationRepository repository;
    private final StageInvitationMapper mapper;

    public StageInvitationService(StageInvitationRepository repository, StageInvitationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public StageInvitationDto sendInvitation(StageInvitationDto dto) {
        StageInvitation invitation = mapper.toEntity(dto);
        invitation.setStatus(StageInvitationStatus.PENDING);
        repository.save(invitation);
        return mapper.toDto(invitation);
    }

    public StageInvitationDto acceptInvitation(Long id) {
        StageInvitation invitation = repository.findById(id);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        repository.save(invitation);
        return mapper.toDto(invitation);
    }

    public StageInvitationDto declineInvitation(Long id, String reason) {
        StageInvitation invitation = repository.findById(id);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(reason);
        repository.save(invitation);
        return mapper.toDto(invitation);
    }

    public List<StageInvitationDto> getUserInvitations(Long userId) {
        return repository.findAll().stream()
                .filter(invitation -> invitation.getInvited() != null && invitation.getInvited().getId().equals(userId))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}

