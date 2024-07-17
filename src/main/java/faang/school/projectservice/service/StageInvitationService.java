package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
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
        stageInvitationDto.setStatus(StageInvitationStatus.PENDING);

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto acceptInvatation(StageInvitationDto stageInvitationDto, long userId) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
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

        Stream<StageInvitation> stageInvitations = stageInvitationRepository.findAll()
                .stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(userId))
                .findAny().stream();
        
        return stageInvitationsFilter.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(stageInvitationFilterDto))
                .reduce(stageInvitations, (cumulativeStream, stageInvitationsFilter) ->
                        stageInvitationsFilter.apply(cumulativeStream, stageInvitationFilterDto), Stream::concat)
                .map(stageInvitationMapper::toDto)
                .toList();
    }
}