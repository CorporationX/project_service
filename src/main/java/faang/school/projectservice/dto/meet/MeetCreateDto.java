package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetCreateDto {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 128, message = "Title cannot be longer than 128 characters")
    private String title;
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 512, message = "Description cannot be longer than 512 characters")
    private String description;
    @Builder.Default
    private MeetStatus status = MeetStatus.PENDING;
    @NotNull
    @Min(value = 1, message = "projectId must be greater than 0")
    private Long projectId;
    @NotNull
    private LocalDateTime startsAt;
}
