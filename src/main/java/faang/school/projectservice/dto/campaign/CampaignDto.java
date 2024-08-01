package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDto {

    @Size(max = 128)
    @NotNull
    private String title;

    @Size(max = 4096)
    private String description;

    @Min(1)
    @NotNull
    private BigDecimal goal;

    @Min(1)
    private BigDecimal amountRaised;

    @NotNull
    private CampaignStatus status;

    @NotNull
    private long projectId;

    @NotNull
    private Currency currency;

    private long createdBy;
    private long updatedBy;
}
