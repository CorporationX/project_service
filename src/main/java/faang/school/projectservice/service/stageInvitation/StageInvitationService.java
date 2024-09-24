package faang.school.projectservice.service.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationDtoMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationDtoMapper stageInvitationDtoMapper;
    private final List<Filter<StageInvitationFilterDto, StageInvitation>> stageInvitationFilters;

    @Transactional
    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationDto.setStatus(StageInvitationStatus.PENDING);
        StageInvitation stageInvitation = stageInvitationRepository
                .save(stageInvitationDtoMapper.toEntity(stageInvitationDto));
        return stageInvitationDtoMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto acceptInvitation(long stageInvitationId) {
        StageInvitation stageInvitation = getStageInvitation(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitation.getStage().getExecutors()
                .add(stageInvitation.getInvited());
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationDtoMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationDto.setStatus(StageInvitationStatus.REJECTED);
        StageInvitation stageInvitation = stageInvitationRepository
                .save(stageInvitationDtoMapper.toEntity(stageInvitationDto));
        return stageInvitationDtoMapper.toDto(stageInvitation);
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> getInvitations(StageInvitationFilterDto filter) {

        return stageInvitationFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .reduce(stageInvitationRepository.findAll().stream(),
                        (s , f) -> f.apply(s, filter),
                        (s1 , s2) -> s1)
                .map(stageInvitationDtoMapper::toDto)
                .toList();
    }

    private StageInvitation getStageInvitation(long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId);
    }
}
