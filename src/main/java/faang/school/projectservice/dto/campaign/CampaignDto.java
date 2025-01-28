package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CampaignDto {
    @NotNull
    @Min(1L)
    Long projectId;

    @NotNull
    String title;
    String description;
    BigDecimal amountRaised;
    @NotNull
    CampaignStatus status;
    @NotNull
    Currency currency;
    boolean removed;
}
