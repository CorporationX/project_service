package faang.school.projectservice.service;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final TeamMember teamMember;

    //private StageExecutionService stageExecutionService;

    @Transactional
    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation = stageInvitationRepository.save(stageInvitation);
        return stageInvitationMapper.toDto(stageInvitation);
    }

    @Transactional
    public StageInvitationDto acceptInvitation(StageInvitationDto stageInvitationDto, TeamMember invited) {
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
    public List<StageInvitationDto> getInvitationsForTeamMemberWithFilters(Long userId, String status, Long authorId) {

        List<StageInvitation> invitations = stageInvitationRepository.findAll(teamMemberId);


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
