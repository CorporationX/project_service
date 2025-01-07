package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignCreateDto {
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    private BigDecimal goal;
    @NotNull
    private Currency currency;
    @NotNull
    private Long projectId;
    @NotNull
    private Long creatorId;
}
