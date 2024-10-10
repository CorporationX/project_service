package faang.school.projectservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {
        private Long id;
        private Long stageId;
        private Long authorId;
        private Long inviteeId;
        private String status;
        private String reason;
}