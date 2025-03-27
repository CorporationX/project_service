package faang.school.projectservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class StageInvitationDto {
    private Long id;
    private Long invitedId;
    private String description;
    private Long authorId;
    private Long stageId;
    private Long statusId;
}
