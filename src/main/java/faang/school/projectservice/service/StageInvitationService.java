package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationRepository stageInvitationRepository;
    private final StageService stageService;
    private final TeamMemberService teamMemberService;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidator;

    public StageInvitationDto sendInvitation(StageInvitationDto stageInvitationDto) {
        Stage stage = stageService.getStageById(stageInvitationDto.getStageId());
        TeamMember invited = teamMemberService.getTeamMember(stageInvitationDto.getInvitedId());
        return createStageInvitationAndGetDto(stage, stageInvitationDto.getAuthorId(), invited);
    }

    public StageInvitationDto createStageInvitationAndGetDto(Stage stage, Long authorId, TeamMember invited) {
        TeamMember author = teamMemberService.getTeamMember(authorId);
        stageInvitationValidator.validateInvitedForCreate(author.getId(), invited.getId());

        StageInvitation invitation = new StageInvitation();
        invitation.setStage(stage);
        invitation.setAuthor(author);
        invitation.setInvited(invited);
        invitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitation saved = stageInvitationRepository.save(invitation);
        return stageInvitationMapper.toDto(saved);
    }
}
