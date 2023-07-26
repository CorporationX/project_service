package faang.school.projectservice.filters.stageInvites;

import faang.school.projectservice.controller.model.stage_invitation.StageInvitationStatus;
import lombok.Data;

@Data
public class FilterStageInviteDto {
    private String descriptionPattern;
    private StageInvitationStatus statusPattern;
    private String stagePattern;
    private Long authorPattern;
    private Long invitedPattern;
}
