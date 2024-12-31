package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetDto {
    private Long id;

    @NotNull
    @Size(max = 128)
    private String title;

    @NotNull
    @Size(max = 512)
    private String description;

    @NotNull
    private MeetStatus status;

    private Long creatorId;

    private Long project;

    @NotNull
    private LocalDateTime date;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
