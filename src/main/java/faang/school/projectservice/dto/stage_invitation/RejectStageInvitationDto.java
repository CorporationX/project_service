package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class RejectStageInvitationDto {
    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long id;

    @NotBlank(message = "Description don`t not black")
    @Size(min = 10, max = 255, message = "Description must be between 10 and 255 characters")
    private String description;
}
