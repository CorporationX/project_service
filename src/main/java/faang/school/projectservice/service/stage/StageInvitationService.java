package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.stage.filter.StageInvitationFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        StageInvitation savedInvitation = stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(savedInvitation);
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> getUserStageInvitations(long userId, StageInvitationFilterDto filters) {
        List<StageInvitation> filteredInvitations = stageInvitationRepository.findAll().stream()
                .filter(invitation -> invitation.getInvited().getId() == userId)
                .filter(invitation -> stageInvitationFilters.stream()
                        .allMatch(filter -> filter.apply(invitation, filters)))
                .collect(Collectors.toList());

        return stageInvitationMapper.toDto(filteredInvitations);
    }

    @Transactional
    public StageInvitationDto accept(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationRepository.findById(stageInvitationDto.getId());

        checkStageInvitationPendingStatus(invitation);
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(savedInvitation);
    }

    @Transactional
    public StageInvitationDto reject(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationRepository.findById(stageInvitationDto.getId());

        checkStageInvitationPendingStatus(invitation);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(stageInvitationDto.getReason());
        StageInvitation savedInvitation = stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(savedInvitation);
    }

    private void checkStageForExists(long stageId) {
        boolean isExist = stageRepository.exist(stageId);
        if (!isExist) {
            log.error("Stage not found by id: {}", stageId);
            throw new EntityNotFoundException(String.format("Stage not found by id: %s", stageId));
        }
    }

    private void checkStageInvitationPendingStatus(StageInvitation invitation) {
        if (invitation.getStatus() != StageInvitationStatus.PENDING) {
            log.error("Only pending invitations can be accepted");
            throw new IllegalStateException("Only pending invitations can be accepted");
        }
    }

    private void validate(StageInvitationDto stageInvitationDto) {
        checkStageForExists(stageInvitationDto.getStageId());
    }
}

