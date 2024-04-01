package faang.school.projectservice.service.stage.impl;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage.StageInvitationService;
import faang.school.projectservice.service.stage.filters.StageInvitationFilter;
import faang.school.projectservice.validator.StageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationRepository stageInvitationRepository;
    private final List<StageInvitationFilter> stageInvitationFilters;
    private final StageValidator stageValidator;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        stageValidator.validate(stageInvitationDto);

        stageInvitationDto.setStatus(StageInvitationStatus.PENDING);
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    public List<StageInvitationDto> getStageInvitations(long id, StageInvitationFilterDto filters) {
        List<StageInvitation> stageInvitations = stageInvitationRepository.findAll();
        stageInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(stageInvitations, filters));

        return stageInvitationMapper.toDto(stageInvitations);
    }

    public StageInvitationDto accept(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationRepository.findById(stageInvitationDto.getId());

        stageValidator.checkStageInvitationPendingStatus(invitation);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }

    public StageInvitationDto reject(StageInvitationDto stageInvitationDto) {
        StageInvitation invitation = stageInvitationRepository.findById(stageInvitationDto.getId());

        stageValidator.checkStageInvitationPendingStatus(invitation);

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(stageInvitationDto.getDescription());
        stageInvitationRepository.save(invitation);

        return stageInvitationMapper.toDto(invitation);
    }
}
