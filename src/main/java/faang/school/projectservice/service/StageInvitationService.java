package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stageInvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.publisher.InviteSentPublisher;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validate.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final InviteSentPublisher inviteSentEvent;

    @Transactional
    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateExecutors(stageInvitationDto);
        StageInvitation model = stageInvitationMapper.toModel(stageInvitationDto);
        StageInvitation saved = stageInvitationRepository.save(model);
        inviteSentEvent.publish(saved);
        return stageInvitationMapper.toDto(saved);
    }

    @Transactional
    public void createStageInvitation(StageInvitation stageInvitation) {
        stageInvitationRepository.save(stageInvitation);
        inviteSentEvent.publish(stageInvitation);
    }

    @Transactional
    public StageInvitationDto accept(long invitationId) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        TeamMember teamMember = teamMemberRepository
                .findById(invitation.getInvited().getId());
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation.getStage().getExecutors().add(teamMember);
        return stageInvitationMapper.toDto(invitation);
    }

    @Transactional
    public StageInvitationDto reject(long invitationId, String message) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(message);
        return stageInvitationMapper.toDto(invitation);
    }

    @Transactional(readOnly = true)
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