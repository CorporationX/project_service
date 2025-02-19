package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampaignFilterDto {
    private Long projectId;
    private String namePattern;
    private BigDecimal minGoal;
    private BigDecimal maxGoal;
    private CampaignStatus status;
    private Long createdBy;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
