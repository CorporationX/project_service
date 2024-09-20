package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.stage.StageInvitationDto;
import faang.school.projectservice.dto.client.stage.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationDtoMapper;
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
    private final List<StageInvitationFilter> stageInvitationFilters;

    public void createInvitation(StageInvitationDto stageInvitationDto) {
        stageInvitationRepository.save(stageInvitationDtoMapper.toEntity(stageInvitationDto));
    }

    @Transactional
    public void acceptInvitation(long stageInvitationId) {
        StageInvitation stageInvitation = getStageInvitation(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(stageInvitation);
        stageInvitation.getStage().getExecutors().add(stageInvitation.getInvited());
    }

    @Transactional
    public void rejectInvitation(long stageInvitationId) {
        StageInvitation stageInvitation = getStageInvitation(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitationRepository.save(stageInvitation);
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> getInvitations(StageInvitationFilterDto filter) {
        Stream<StageInvitation> invitationStream = stageInvitationRepository.findAll().stream();
        if (filter != null)
            invitationStream = stageInvitationFilters.stream()
                    .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(filter))
                    .reduce(invitationStream,
                            (stream, stageInvitationFilters) -> stageInvitationFilters.apply(stream, filter),
                            (s1, s2) -> s1);

        return stageInvitationDtoMapper.toDtos(invitationStream.toList());
    }

    private StageInvitation getStageInvitation(long stageInvitationId) {
        return stageInvitationRepository.findById(stageInvitationId);
    }
}
