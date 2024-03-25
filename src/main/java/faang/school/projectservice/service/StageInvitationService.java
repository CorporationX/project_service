package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import faang.school.projectservice.model.stage.Stage;

@RequiredArgsConstructor
@Service
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;

    public StageInvitationDto sendInvitation(Long stageId, Long authorId, Long invitedId) {
        Stage stage = stageRepository.getById(stageId);
        TeamMember author = teamMemberRepository.findById(authorId);
        TeamMember invited = teamMemberRepository.findById(invitedId);
        StageInvitation stageInvitation = StageInvitation.builder()
                .invited(invited)
                .stage(stage)
                .author(author)
                .status(StageInvitationStatus.PENDING)
                .description()
                .build();
    }
}
