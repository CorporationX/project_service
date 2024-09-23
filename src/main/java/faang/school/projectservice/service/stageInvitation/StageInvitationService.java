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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationDtoMapper stageInvitationDtoMapper;
    private final List<Filter<StageInvitationFilterDto, StageInvitation>> stageInvitationFilters;

    public StageInvitationDto createInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository
                .save(stageInvitationDtoMapper.toEntity(stageInvitationDto));
        return stageInvitationDtoMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto acceptInvitation(long stageInvitationId) {
        StageInvitation stageInvitation = getStageInvitation(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(stageInvitation);
        stageInvitation.getStage().getExecutors()
                .add(stageInvitation.getInvited());
        return stageInvitationDtoMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto rejectInvitation(long stageInvitationId, String description) {
        StageInvitation stageInvitation = getStageInvitation(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(description);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationDtoMapper.toDto(stageInvitation);
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> getInvitations(StageInvitationFilterDto filter) {
        Stream<StageInvitation> invitationStream = stageInvitationRepository.findAll().stream();

        return stageInvitationFilters.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(filter))
                .flatMap(stageInvitationFilter -> stageInvitationFilter.apply(invitationStream, filter))
                .map(stageInvitationDtoMapper::toDto)
                .toList();
    }

    private StageInvitation getStageInvitation(long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId);
    }
}
