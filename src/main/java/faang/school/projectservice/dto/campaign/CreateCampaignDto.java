package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.validation.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreateCampaignDto(
        @NotBlank(message = "Title must not be blank")
        @Size(max = ValidationConstants.TITLE_MAX_LENGTH, message = ValidationConstants.TITLE_SIZE_MESSAGE)
        String title,

        @NotBlank(message = "Description must not be blank")
        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
        String description,

        @NotNull(message = "Goal amount should be present")
        BigDecimal goal,

        @NotNull(message = "Project id should be present")
        Long projectId,

        @NotNull(message = "Currency should be present")
        Currency currency
) {
}