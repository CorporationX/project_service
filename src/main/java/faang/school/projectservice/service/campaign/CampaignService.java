package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.Campaign;

import java.util.List;

public interface CampaignService {
    void createCampaign(CampaignDto campaignDto);
    void updateCampaign(CampaignDto campaignDto, Long id);
    void deleteCampaign(Long id);
    CampaignDto findById (Long id);
    List<CampaignDto> findAll(CampaignFilterDto campaignFilterDto);
}
