package faang.school.projectservice.filter.stageinvite;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FilterStageInviteDto {
    private String descriptionPattern;
    private StageInvitationStatus statusPattern;
    private String stagePattern;
    private Long authorPattern;
    private Long invitedPattern;
}
