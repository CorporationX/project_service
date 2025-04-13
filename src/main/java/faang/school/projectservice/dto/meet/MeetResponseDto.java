package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetResponseDto {

    private long id;
    private String title;
    private String description;
    private MeetStatus status;
    private long creatorId;
    private long projectId;
    private List<Long> userIds;
    private LocalDateTime startsAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
