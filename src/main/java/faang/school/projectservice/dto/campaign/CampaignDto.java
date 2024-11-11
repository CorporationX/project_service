package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDto {
    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private BigDecimal goal;
    private BigDecimal amountRaised;
    private CampaignStatus status;
    private LocalDateTime createdAt;
}
