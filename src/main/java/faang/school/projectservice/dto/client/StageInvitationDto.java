package faang.school.projectservice.dto.client;

import lombok.Data;


@Data
public class StageInvitationDto {
    private long id;
    private String description;
    private String status;
    private String rejectionReason;
    private long stageId;
    private long authorId;
    private long invitedId;
}