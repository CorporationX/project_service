package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@FieldNameConstants
@Builder
public record CampaignFilterDto(
        LocalDateTime createdAt,
        CampaignStatus status,
        Long createdBy
) {
    @AssertTrue(message = "At least one filter criteria must be provided")
    public boolean isAtLeastOneFilterPresent() {
        return createdAt != null || status != null || createdBy != null;
    }
}