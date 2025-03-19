package faang.school.projectservice.dto.stageinvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationFilterDto {

    private String descriptionPattern;
    private StageInvitationStatus status;

    @Positive
    private Long stageId;

    @Positive
    private Long authorId;

    @Positive
    private Long invitedId;
}
