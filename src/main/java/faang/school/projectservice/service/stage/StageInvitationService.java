package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.filters.StageInvitationFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final List<StageInvitationFilter> stageInvitationFilters;

    @Transactional
    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        validate(stageInvitationDto);

        stageInvitationDto.setStatus(StageInvitationStatus.PENDING);
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    @Transactional
    public List<StageInvitationDto> getStageInvitations(long id, StageInvitationFilterDto filters) {
        List<StageInvitation> stageInvitations = stageInvitationRepository.findAll();
        stageInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(stageInvitations, filters));

        return stageInvitationMapper.toDto(stageInvitations);
    }

    @Transactional
    public StageInvitationDto accept(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationRepository.findById(stageInvitationDto.getId());

        checkStageInvitationPendingStatus(invitation);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }

    @Transactional
    public StageInvitationDto reject(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationRepository.findById(stageInvitationDto.getId());

        checkStageInvitationPendingStatus(invitation);

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(stageInvitationDto.getReason());
        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }

    private void checkStageForExists(long stageId) {
        boolean isExist = stageRepository.exist(stageId);

        if (!isExist) {
            throw new EntityNotFoundException(String.format("Stage not found by id: %s", stageId));
        }
    }

    private void checkStageInvitationPendingStatus(StageInvitation invitation) {
        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new IllegalStateException("Only pending invitations can be accepted");
        }
    }

    private void validate(StageInvitationDto stageInvitationDto) {
        checkStageForExists(stageInvitationDto.getStageId());
    }
}

