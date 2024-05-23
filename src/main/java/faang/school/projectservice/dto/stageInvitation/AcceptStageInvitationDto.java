package faang.school.projectservice.dto.stageInvitation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptStageInvitationDto {
    @NotNull(message = "The invitation id can't be empty")
    private Long id;
}
