package faang.school.projectservice.service;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.dto.filter.CampaignFilterDto;

import java.util.List;

public interface CampaignService {

    CampaignDto publishCampaign(CampaignDto campaignDto);

    void updateCampaign(CampaignUpdateDto campaignDto);

    void deleteCampaign(long id);

    CampaignDto getCampaign(long id);

    List<CampaignDto> getCampaignsSortedByCreatedAtAndByFilter(CampaignFilterDto campaignFilterDto);
}
