package faang.school.projectservice.service.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stageInvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<StageInvitationFilter> stageInvitationsFilter;

    public StageInvitationDto sendAnInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto acceptInvatation(Long stageInvitationId, long userId) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationId);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        teamMemberRepository.findById(stageInvitation.getInvited().getId()).setUserId(userId);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto declineTheInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(stageInvitationDto.getDescription());
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> getStageInvitationForUser(StageInvitationFilterDto stageInvitationFilterDto,
                                                              long userId) {
        if (stageInvitationFilterDto == null) {
            log.error("filter is null");
            throw new IllegalArgumentException("filter is null");
        }

        Stream<StageInvitation> stageInvitations = stageInvitationRepository.findByIdAllInvited(userId).stream();

        return stageInvitationsFilter.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(stageInvitationFilterDto))
                .reduce(stageInvitations, (cumulativeStream, stageInvitationsFilter) ->
                        stageInvitationsFilter.apply(cumulativeStream, stageInvitationFilterDto), Stream::concat)
                .map(stageInvitationMapper::toDto)
                .toList();
    }

    public List<StageInvitationDto> sendStageInvitations(List<StageInvitationDto> invitations) {
        List<StageInvitation> toSave = invitations.stream()
                .peek(invitation -> {
                    invitation.setStatus(StageInvitationStatus.PENDING);
                })
                .map(stageInvitationMapper::toEntity)
                .toList();

        List<StageInvitation> result = stageInvitationRepository.saveAll(toSave);

        return result.stream()
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}