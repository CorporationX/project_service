package faang.school.projectservice.dto.stage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {

        private Long id;

        @NotNull(message = "Stage ID cannot be null")
        private Long stageId;

        @NotNull(message = "Author ID cannot be null")
        private Long authorId;

        @NotNull(message = "Invitee ID cannot be null")
        private Long inviteeId;

        @NotBlank(message = "Status cannot be blank")
        @Size(max = 255, message = "Status cannot exceed 255 characters")
        private String status;

        @Size(max = 255, message = "Reason cannot exceed 255 characters")
        private String reason;
}