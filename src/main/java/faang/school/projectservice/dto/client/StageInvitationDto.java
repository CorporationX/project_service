package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class StageInvitationDto {
    private long id;
    private String description;
    private String status;
    @NotNull
    private String rejectionReason;
    private long stageId;
    private long authorId;
    private long invitedId;
}