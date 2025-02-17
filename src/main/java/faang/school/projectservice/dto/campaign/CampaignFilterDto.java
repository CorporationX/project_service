package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampaignFilterDto {
    private String namePattern;
    private CampaignStatus status;
    private Long createdBy;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
