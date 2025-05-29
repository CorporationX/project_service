package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
public class CampaignDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private CampaignStatus status;
    private Project project;
    private Currency currency;
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
}
