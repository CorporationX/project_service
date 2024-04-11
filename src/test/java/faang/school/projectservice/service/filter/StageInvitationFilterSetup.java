package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.filter.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class StageInvitationFilterSetup {
    StageInvitationFilterDto filter1;
    StageInvitationFilterDto filter2;
    StageInvitationFilterDto filter3;
    StageInvitation stageInvitation1;
    StageInvitation stageInvitation2;
    StageInvitation stageInvitation3;

    @BeforeEach
    void setup() {
        Long stageId = 100L;
        Long authorId = 200L;
        Long invitedId = 300L;
        Long invitationId1 = 400L;
        Long invitationId2 = 500L;
        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(authorId);
        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(invitedId);
        Stage stage = new Stage();
        stage.setStageId(stageId);
        stage.setExecutors(new ArrayList<>());
        stageInvitation1 = StageInvitation.builder()
                .invited(teamMember1)
                .author(teamMember2)
                .stage(stage)
                .description("")
                .id(invitationId1)
                .status(StageInvitationStatus.ACCEPTED)
                .build();
        stageInvitation2 = StageInvitation.builder()
                .invited(teamMember2)
                .author(teamMember1)
                .stage(stage)
                .description("")
                .id(invitationId2)
                .status(StageInvitationStatus.REJECTED)
                .build();
        stageInvitation3 = StageInvitation.builder()
                .invited(teamMember1)
                .author(teamMember2)
                .stage(stage)
                .description("")
                .id(invitationId2)
                .status(StageInvitationStatus.REJECTED)
                .build();
        filter1 = StageInvitationFilterDto.builder()
                .teamMemberPattern(authorId)
                .build();
        filter2 = StageInvitationFilterDto.builder()
                .statusPattern(StageInvitationStatus.REJECTED)
                .build();
        filter3 = StageInvitationFilterDto.builder()
                .teamMemberPattern(authorId)
                .statusPattern(StageInvitationStatus.REJECTED)
                .build();
    }

}
