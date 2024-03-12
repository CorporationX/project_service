package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StageInvitationDto {

    private Long id;
    private String description;
    private StageInvitationStatus status;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;

}
