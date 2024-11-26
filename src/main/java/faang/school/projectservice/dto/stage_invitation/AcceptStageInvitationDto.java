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
public class AcceptStageInvitationDto {
    @Positive(message = "Id must be positive")
    @NotNull(message = "Id must not be null")
    private Long id;

    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
