package faang.school.projectservice.service;

import  com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDTO;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.filter.InvitationFilter;
import faang.school.projectservice.validation.StageInvitationValidation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<InvitationFilter> invitationFilters;
    private final StageInvitationValidation stageInvitationValidation;

    @Override
    public void sendInvitation(@Valid StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitationRepository.save(stageInvitation);
    }

    @Override
    public void acceptInvitation(Long invitationId) {
        StageInvitation stageInvitation = stageInvitationValidation.validateInvitationExists(invitationId);
        if (stageInvitation.getStatus() == StageInvitationStatus.PENDING) {
            stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
            stageInvitationRepository.save(stageInvitation);
        } else {
            throw new IllegalStateException("Cannot accept invitation with status: " + stageInvitation.getStatus());
        }
    }

    @Override
    public void rejectInvitationWithReason(Long invitationId, String rejectionReason) {

        StageInvitation stageInvitation = stageInvitationRepository.findById(invitationId);
        if (stageInvitation == null) {
            throw new NotFoundException("Invitation not found with id: " + invitationId);
        }
        stageInvitation.setDescription(rejectionReason);

        if (stageInvitation.getStatus() == StageInvitationStatus.PENDING) {
            stageInvitation.setStatus(StageInvitationStatus.REJECTED);
            stageInvitationRepository.save(stageInvitation);
        } else {
            throw new IllegalStateException("Cannot reject invitation with status: " + stageInvitation.getStatus());
        }

    }

    @Override
    public List<StageInvitationDto> getInvitationsWithFilters(StageInvitationFilterDTO filterDTO) {
        List<StageInvitation> invitations = stageInvitationRepository.findAll();
        Stream<StageInvitation> invitationStream = invitations.stream();

        List<InvitationFilter> applicableFilters = invitationFilters.stream()
                .filter(filter -> filter.isApplicable(filterDTO)).toList();

        for (InvitationFilter filter : applicableFilters) {
            invitationStream = filter.filter(invitationStream, filterDTO);
        }

        return invitationStream.map(stageInvitationMapper::toDto).toList();
    }
}
