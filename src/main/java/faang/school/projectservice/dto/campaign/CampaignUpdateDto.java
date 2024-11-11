package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignUpdateDto {
    private String title;
    private String description;
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private CampaignStatus status;
}
