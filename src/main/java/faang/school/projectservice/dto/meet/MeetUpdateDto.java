package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetUpdateDto {

    private String title;
    private String description;
    private MeetStatus status;
    private LocalDateTime startsAt;
    @NotNull
    @Min(value = 1, message = "ProjectId must be greater than 0")
    private Long projectId;
    @NotNull
    @Min(value = 1, message = "Meet Id must be greater than 0")
    private Long id;
}
