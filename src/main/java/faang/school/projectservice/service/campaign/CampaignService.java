package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;

public interface CampaignService {
    CampaignDto getCampaignById(long campaignId);
}