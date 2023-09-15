package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StageInvitationDto {
    @NotBlank(message = "stage name can't be empty")
    @Size(min = 1, max = 1000, message = "stage name the value cannot be empty or more than 1000 characters")
    private String description;
    private StageInvitationStatus status;
    @Positive(message = "user id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807")
    private Long idAuthor;
    @Positive(message = "user id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807")
    private Long idInvited;
    @Valid
    private DtoStage stage;

    public StageInvitationDto(String description, long userIdAuthor, long userIdInvited, DtoStage stage) {
        this.description = description;
        this.status = StageInvitationStatus.PENDING;
        this.idAuthor = userIdAuthor;
        this.idInvited = userIdInvited;
        this.stage = stage;
    }
}
