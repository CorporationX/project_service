package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class StageInvitationDto {
    @Min(1)
    @Max(Long.MAX_VALUE)
    private Long id;
    private String status;
    @NonNull
    private Long stageId;
    @NonNull
    private Long authorId;
    @NonNull
    private Long invitedId;
}