package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.CampaignStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CampaignFilterDto {
    private LocalDateTime createdAtAfter;
    private LocalDateTime createdAtBefore;
    private CampaignStatus status;
    private Long createdBy;
}