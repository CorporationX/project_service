package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stageInvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validate.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final List<StageInvitationFilter> stageInvitationFilterList;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateExecutors(stageInvitationDto);
        return stageInvitationMapper.toDto(stageInvitationRepository
                .save(stageInvitationMapper.toModel(stageInvitationDto)));
    }

    public StageInvitationDto accept(long invitationId) {
        StageInvitation toUpdate = stageInvitationRepository.findById(invitationId);
        toUpdate.setStatus(StageInvitationStatus.ACCEPTED);
        TeamMember teamMember = teamMemberRepository.findById(toUpdate.getInvited().getId());
        toUpdate.getStage().getExecutors().add(teamMember);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(toUpdate));
    }

    public StageInvitationDto reject(long invitationId, String message) {
        StageInvitation toReject = stageInvitationRepository.findById(invitationId);
        toReject.setStatus(StageInvitationStatus.REJECTED);
        toReject.setDescription(message);
        return stageInvitationMapper.toDto(stageInvitationRepository.save(toReject));
    }

    public List<StageInvitationDto> getStageInvitationFilter(
            StageInvitationFilterDto stageInvitationFilterDto, long userId) {
        Stream<StageInvitation> stageInvitationStream = stageInvitationRepository.findAll().stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(userId));
        List<StageInvitationFilter> requestForFilters = stageInvitationFilterList.stream()
                .filter(stageInvitationFilter -> stageInvitationFilter.isApplicable(stageInvitationFilterDto)).toList();
        for (StageInvitationFilter filter : requestForFilters) {
            stageInvitationStream = filter.apply(stageInvitationStream, stageInvitationFilterDto);
        }
        return stageInvitationStream.map(stageInvitationMapper::toDto).toList();
    }
}
