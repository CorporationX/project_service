package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;
    private final Validator validator;

    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        validator.validateStageInvitationDto(stageInvitationDto);

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto, TeamMember invited) {
        validator.validateStageInvitationDto(stageInvitationDto);

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);

        Stage stage = stageInvitation.getStage();
        List<TeamMember> executors = stage.getExecutors();
        executors.add(invited);

        stageInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto rejectInvitation(StageInvitationDto stageInvitationDto, TeamMember invited, String rejectionReason) {
        validator.validateStageInvitationDto(stageInvitationDto);

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);

        Stage stage = stageInvitation.getStage();
        List<TeamMember> executors = stage.getExecutors();
        executors.remove(invited);

        stageInvitation.setRejectionReason(rejectionReason);

        stageInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional(readOnly = true)
    public List<StageInvitationDto> getInvitationsForTeamMemberWithFilters(Long teamMemberId, String status, Long authorId) {
        Long currentUserId = userContext.getUserId();
        TeamMember currentUser = teamMemberRepository.findById(currentUserId);
        if (currentUser == null) {
            throw new RuntimeException("Current user is null.");
        }
        validator.validateTeamMemberId(teamMemberId);

        if (!currentUser.getId().equals(teamMemberId)) {
            try {
                throw new AccessDeniedException("You don't have permission to view invitations for this team member");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        validator.validateInvitationsFilterParams(teamMemberId, status, authorId);
        List<StageInvitation> invitations = stageInvitationRepository.findByInvitedId(teamMemberId);

        if (status != null && !status.isEmpty()) {
            invitations = invitations.stream()
                    .filter(invitation -> invitation.getStatus().toString().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        if (authorId != null) {
            invitations = invitations.stream()
                    .filter(invitation -> invitation.getAuthor().getId().equals(authorId))
                    .collect(Collectors.toList());
        }
        return invitations.stream()
                .map(stageInvitationMapper::toDto)
                .collect(Collectors.toList());
    }
}