package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.Stage;
import lombok.Data;

@Data
public class StageInvitationDto {
    private Long id;
    private Stage stage;
    private Long authorId;
    private Long invitedId;
}