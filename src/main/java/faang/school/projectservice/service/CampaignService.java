package faang.school.projectservice.service;

import faang.school.projectservice.model.dto.campaign.CampaignDto;
import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.entity.Campaign;

import java.util.List;

public interface CampaignService {
    CampaignDto publishCampaign(CampaignDto campaignDto);

    CampaignDto updateCampaign(CampaignUpdateDto campaignDto);

    void deleteCampaign(long id);

    CampaignDto getCampaign(long id);

    List<CampaignDto> getAllCampaignsByFilter(CampaignFilterDto filters);

    Campaign findCampaignById(long id);
}
