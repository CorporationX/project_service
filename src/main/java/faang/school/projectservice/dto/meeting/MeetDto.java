package faang.school.projectservice.dto.meeting;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetDto {
    @Positive
    private Long id;
    @NotNull
    private Long projectId;
    @NotBlank
    @Size(max = 128)
    private String title;
    @NotBlank
    @Size(max = 512)
    private String description;
    private String status;
    private boolean active;
    private LocalDateTime createdAt;
    @NotNull
    @FutureOrPresent
    private LocalDateTime scheduledAt;
    private LocalDateTime updatedAt;
}
