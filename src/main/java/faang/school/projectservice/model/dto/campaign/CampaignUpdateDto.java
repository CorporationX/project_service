package faang.school.projectservice.model.dto.campaign;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CampaignUpdateDto(
        @NotNull
        @Positive
        Long id,
        @Size(min = 1, max = 256)
        String title,
        @Size(max = 4096)
        String description
) {
}
