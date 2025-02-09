package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record MeetDto(
        Long id,

        @NotBlank
        @Size(max = 128)
        String title,

        @NotBlank
        @Size(max = 512)
        String description,

        @NotNull
        MeetStatus status,

        @NotNull
        @Positive
        Long creatorId,

        @NotNull
        @Positive
        Long projectId,

        @NotEmpty
        List<Long> userIds,

        @NotNull
        @Future
        LocalDateTime startsAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt){}