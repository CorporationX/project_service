package faang.school.projectservice.dto.filter;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampaignFilterDto {
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private CampaignStatus status;
    private Long creatorId;
}
