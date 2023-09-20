package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.Currency;
import faang.school.projectservice.model.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignGetDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private CampaignStatus status;
    private long projectId;
    private Currency currency;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
