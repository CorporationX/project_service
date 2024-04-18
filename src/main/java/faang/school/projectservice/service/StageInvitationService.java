package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.StageInvitationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final List<StageInvitationFilter> stageInvitationFilters;

    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        Stage stage = stageRepository.getById(stageInvitationDto.getStageId());
        TeamMember author = teamMemberRepository.findById(stageInvitationDto.getAuthorId());
        TeamMember invited = teamMemberRepository.findById(stageInvitationDto.getInvitedId());
        StageInvitation stageInvitation = StageInvitation.builder()
                .invited(invited)
                .stage(stage)
                .author(author)
                .status(StageInvitationStatus.PENDING)
                .build();
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    public void acceptInvitation(Long invitationId) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        TeamMember invited = invitation.getInvited();
        Stage stage = invitation.getStage();
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        List<TeamMember> executors = stage.getExecutors();
        executors.add(invited);
        stageInvitationRepository.save(invitation);
    }

    public void declineInvitation(Long invitationId, String reason) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        StringBuilder builder = new StringBuilder(invitation.getDescription());
        builder.append(" rejected: " + reason);
        invitation.setDescription(builder.toString());
        stageInvitationRepository.save(invitation);
    }

    public List<StageInvitationDto> getAllInvitationsForUserWithStatus(StageInvitationFilterDto filters) {
        List<StageInvitation> stageInvitations = stageInvitationRepository.findAll();
        List<StageInvitationFilter> invitationFilters = stageInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();
        return  stageInvitations.stream()
                .filter(stageInvitation -> invitationFilters.stream()
                        .allMatch(stageInvitationFilter -> stageInvitationFilter.apply(stageInvitation, filters)))
                .map(stageInvitationMapper::toDto)
                .toList();

    }
}
