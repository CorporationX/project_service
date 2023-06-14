package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampaignFilterDto {
    private String namePattern;
    private BigDecimal minGoal;
    private BigDecimal maxGoal;
    private CampaignStatus status;
    private int page = 0;
    private int pageSize = 10;
}
