package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StageInvitationDto {

    @NotNull(message = "User missing from request")
    private Long id;
    @NotEmpty(message = "Description missing from request")
    private String description;
    private StageInvitationStatus status;
    @NotNull(message = "Stage missing from request")
    private Stage stage;
    @NotNull(message = "Author missing from request")
    private TeamMember author;
    @NotNull(message = "Invited missing from request")
    private TeamMember invited;

}
