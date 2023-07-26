package faang.school.projectservice.dto;

import faang.school.projectservice.controller.model.TeamMember;
import faang.school.projectservice.controller.model.stage.Stage;
import faang.school.projectservice.controller.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Data
@Component
public class StageInvitationDto {
    @Min(0)
    private Long id;
    @NonNull
    private String description;
    @NonNull
    private Stage stage;
    @NonNull
    private TeamMember invited;
}
