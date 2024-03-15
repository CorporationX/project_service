package faang.school.projectservice.service.stage.impl;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage.StageInvitationService;
import faang.school.projectservice.service.stage.filters.StageInvitationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationServiceImpl implements StageInvitationService {
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationRepository stageInvitationRepository;
    private final List<StageInvitationFilter> stageInvitationFilters;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
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
        stageInvitationDto.setStatus(StageInvitationStatus.ACCEPTED);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitationMapper.toEntity(stageInvitationDto)));
    }

    public StageInvitationDto reject(StageInvitationDto stageInvitationDto) {
        stageInvitationDto.setStatus(StageInvitationStatus.REJECTED);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitationMapper.toEntity(stageInvitationDto)));
    }
}
