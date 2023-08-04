package faang.school.projectservice.dto;

import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class StageInvitationDto {
    private Long id;
    private String description;
    private StageInvitationStatus status;
    private Stage stage;
}
