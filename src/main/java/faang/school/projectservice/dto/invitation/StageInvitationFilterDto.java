package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationFilterDto {
    @Min(value = 1, message = "AuthorId must be a positive value.")
    private Long authorId;

    @Pattern(regexp = "^(PENDING|REJECTED|ACCEPTED)$", message = "Invalid status value. Valid values are PENDING, REJECTED, ACCEPTED.")
    private String status;
}
