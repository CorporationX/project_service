package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.validation.ValidationGroups;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StageInvitationDto {

    @NotNull(message = "StageInvitation's 'id' can not be null", groups = {ValidationGroups.Update.class})
    @Positive(message = "StageInvitation's 'id' should be greater than zero", groups = {ValidationGroups.Update.class})
    private Long id;
    @Size(max = 255, message = "StageInvitation's 'description' can not be greater than 255 symbols.",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String description;
    @NotNull(message = "StageInvitation's 'status' can not be null", groups = {ValidationGroups.Update.class})
    private StageInvitationStatus status;
    private Stage stage;
    private TeamMember author;
    private TeamMember invited;

}
