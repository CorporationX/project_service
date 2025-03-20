package faang.school.projectservice.dto;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.model.CampaignStatus;

public class CampaignDto {
    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private CampaignStatus status;
    private Currency currency;
    private Long updatedBy;
}
