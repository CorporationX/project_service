package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;

import java.util.List;

public interface CampaignService {
    CampaignDto create(CreateCampaignDto createCampaignDto);

    CampaignDto update(long campaignId, UpdateCampaignDto updateCampaignDto);

    void softDelete(long campaignId);

    CampaignDto getCampaignById(long campaignId);

    List<CampaignDto> getByFilters(CampaignFilterDto campaignFilterDto);
}