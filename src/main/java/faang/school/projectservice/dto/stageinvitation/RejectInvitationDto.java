package faang.school.projectservice.dto.stageinvitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectInvitationDto {

    @NotNull
    private ChangeStatusDto statusDto;

    @NotNull
    @NotBlank
    private String reasonForReject;
}
